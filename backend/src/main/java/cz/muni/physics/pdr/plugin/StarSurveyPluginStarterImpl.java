package cz.muni.physics.pdr.plugin;

import cz.muni.physics.pdr.entity.PhotometricData;
import cz.muni.physics.pdr.entity.Plugin;
import cz.muni.physics.pdr.entity.StarSurvey;
import cz.muni.physics.pdr.manager.StarSurveyManager;
import cz.muni.physics.pdr.resolver.StarResolverResult;
import cz.muni.physics.pdr.utils.ParameterUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

/**
 * @author Michal Krajčovič
 * @version 1.0
 * @since 08/04/16
 */
@Component
@Scope("prototype")
public class StarSurveyPluginStarterImpl implements StarSurveyPluginStarter {

    private final static Logger logger = LogManager.getLogger(StarSurveyPluginStarterImpl.class);

    @Autowired
    private StarSurveyManager starSurveyManager;
    @Autowired
    private ThreadPoolTaskExecutor searchServiceExecutor;

    private Consumer<StarSurvey> onNoResultsFound;
    private Consumer<StarSurvey> onResultsFound;

    public Map<StarSurvey, List<PhotometricData>> runAll(StarResolverResult resolverResult) {
        Map<StarSurvey, List<PhotometricData>> resultMap = new HashMap<>();
        List<CompletableFuture> futures = new ArrayList<>();
        for (StarSurvey survey : starSurveyManager.getAll()) {
            if (survey.getPlugin() == null || survey.getPlugin().getName().isEmpty()) {
                logger.debug("No plugin set for {} star survey, skipping", survey.getName());
                continue;
            }
            Map<String, String> params = ParameterUtils.resolveParametersForSurvey(survey, resolverResult);
            logger.debug("Starting task for {}", survey.getName());
            futures.add(run(survey.getPlugin(), params)
                    .thenAccept(data -> {
                        logger.debug("Found {} entries from {} star survey", data.size(), survey.getName());
                        if (!data.isEmpty()) {
                            resultMap.put(survey, data);
                            if (onNoResultsFound != null)
                                onNoResultsFound.accept(survey);
                        } else {
                            if (onResultsFound != null)
                                onResultsFound.accept(survey);
                        }
                    }));
        }
        futures.forEach(future -> {
            try {
                if (future != null) future.get();
            } catch (InterruptedException | ExecutionException e) {
                logger.error("Failed to wait for result for all star survey tasks.", e);
            }
        });
        return resultMap;
    }

    private CompletableFuture<List<PhotometricData>> run(Plugin plugin, Map<String, String> params) {
        if (plugin == null) {
            throw new IllegalArgumentException("Plugin is null");
        }
        if (params == null) {
            throw new IllegalArgumentException("params cannot be null.");
        }
        ProcessStarter<PhotometricData> starter = new PhotometricDataProcessStarter();
        if (!starter.prepare(plugin.getCommands(), params)) {
            logger.debug("Not able to prepare {} plugin command", plugin.getName());
            return CompletableFuture.completedFuture(Collections.emptyList());
        }
        return CompletableFuture.supplyAsync(() -> starter.runForResult(searchServiceExecutor), searchServiceExecutor);
    }


    public Consumer<StarSurvey> getOnNoResultsFound() {
        return onNoResultsFound;
    }

    @Override
    public void setOnNoResultsFound(Consumer<StarSurvey> onNoResultsFound) {
        this.onNoResultsFound = onNoResultsFound;
    }

    public Consumer<StarSurvey> getOnResultsFound() {
        return onResultsFound;
    }

    @Override
    public void setOnResultsFound(Consumer<StarSurvey> onResultsFound) {
        this.onResultsFound = onResultsFound;
    }
}
