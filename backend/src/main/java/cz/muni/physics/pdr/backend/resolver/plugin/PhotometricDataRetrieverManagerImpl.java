package cz.muni.physics.pdr.backend.resolver.plugin;

import cz.muni.physics.pdr.backend.entity.PhotometricData;
import cz.muni.physics.pdr.backend.entity.Plugin;
import cz.muni.physics.pdr.backend.entity.StarSurvey;
import cz.muni.physics.pdr.backend.entity.StellarObject;
import cz.muni.physics.pdr.backend.manager.StarSurveyManager;
import cz.muni.physics.pdr.backend.utils.ParameterUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Consumer;

/**
 * @author Michal Krajčovič
 * @version 1.0
 * @since 08/04/16
 */
public class PhotometricDataRetrieverManagerImpl implements PhotometricDataRetrieverManager {

    private static final Logger logger = LogManager.getLogger(PhotometricDataRetrieverManagerImpl.class);

    private StarSurveyManager starSurveyManager;
    private Executor executor;

    private double minThreshold;
    private double maxThreshold;

    private Consumer<PluginSearchFinishResult> onSearchFinish;
    private List<CompletableFuture> futures = null;

    public PhotometricDataRetrieverManagerImpl(StarSurveyManager starSurveyManager,
                                               Executor executor,
                                               double minThreshold,
                                               double maxThreshold) {
        this.starSurveyManager = starSurveyManager;
        this.executor = executor;
        this.minThreshold = minThreshold;
        this.maxThreshold = maxThreshold;
    }

    public Map<StarSurvey, List<PhotometricData>> runAll(StellarObject resolverResult) {
        Map<StarSurvey, List<PhotometricData>> resultMap = new HashMap<>();
        futures = new ArrayList<>();
        for (StarSurvey survey : starSurveyManager.searchForAll(StarSurvey::isEnabled)) {
            if (survey.getPlugin() == null || survey.getPlugin().getName().isEmpty()) {
                logger.debug("No plugin set for {} star survey, skipping", survey.getName());
                continue;
            }
            Map<String, String> params = ParameterUtils.resolveParametersForSurvey(survey, resolverResult, new ArrayList<>(starSurveyManager.getAllPatterns().values()));
            futures.add(run(survey.getPlugin(), params)
                    .thenAccept(data -> {
                        logger.debug("Found {} entries from {} star survey", data.size(), survey.getName());
                        resultMap.put(survey, data);

                        onSearchFinish.accept(new PluginSearchFinishResult(survey.getName(), data.size()));
                    }));
        }
        CompletableFuture[] cfs = futures.toArray(new CompletableFuture[futures.size()]);
        try {
            CompletableFuture.allOf(cfs).join();
        } catch (CancellationException exc) {
            logger.error("Photometric Data Retriever tasks were cancelled", exc);
        }
        return resultMap;
    }

    private CompletableFuture<List<PhotometricData>> run(Plugin plugin, Map<String, String> params) {
        if (plugin == null) {
            throw new IllegalArgumentException("Plugin is null");
        }
        if (params == null) {
            throw new IllegalArgumentException("params cannot be null.");
        }
        logger.debug("Running plugin {}", plugin.getName());
        ProcessStarter<List<PhotometricData>> starter = new PhotometricDataProcessStarter(minThreshold, maxThreshold);
        if (!starter.prepare(plugin.getCommands(), params)) {
            logger.debug("Not able to prepare {} plugin command", plugin.getName());
            return CompletableFuture.completedFuture(new ArrayList<>());
        }
        return CompletableFuture.supplyAsync(starter::runForResult, executor);
    }

    @Override
    public void cancelAll() {
        logger.debug("Cancelling all plugin tasks");
        if (futures != null) {
            futures.stream().filter(future -> future != null && !future.isDone()).forEach(future -> {
                future.cancel(true);
            });
        }
    }

    @Override
    public void setOnSearchFinish(Consumer<PluginSearchFinishResult> onSearchFinish) {
        this.onSearchFinish = onSearchFinish;
    }
}
