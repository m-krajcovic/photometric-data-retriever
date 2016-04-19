package cz.muni.physics.pdr.backend.resolver.plugin;

import cz.muni.physics.pdr.backend.entity.PhotometricData;
import cz.muni.physics.pdr.backend.entity.Plugin;
import cz.muni.physics.pdr.backend.entity.StarSurvey;
import cz.muni.physics.pdr.backend.entity.StellarObject;
import cz.muni.physics.pdr.backend.exception.ResourceAvailabilityException;
import cz.muni.physics.pdr.backend.manager.StarSurveyManager;
import cz.muni.physics.pdr.backend.utils.ParameterUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import java.util.function.Consumer;

/**
 * @author Michal Krajčovič
 * @version 1.0
 * @since 08/04/16
 */
@Component
@Scope("prototype")
public class PhotometricDataRetrieverManagerImpl implements PhotometricDataRetrieverManager {

    private final static Logger logger = LogManager.getLogger(PhotometricDataRetrieverManagerImpl.class);

    @Autowired
    private StarSurveyManager starSurveyManager;
    @Autowired
    private Executor searchServiceExecutor;

    private Consumer<StarSurvey> onNoResultsFound;
    private Consumer<StarSurvey> onResultsFound;

    private List<Future> futures = null;
    private List<ProcessStarter> processStarters;

    public Map<StarSurvey, List<PhotometricData>> runAll(StellarObject resolverResult) throws ResourceAvailabilityException {
        Map<StarSurvey, List<PhotometricData>> resultMap = new HashMap<>();
        futures = new ArrayList<>();
        processStarters = new ArrayList<>();
        for (StarSurvey survey : starSurveyManager.getAll()) {
            if (survey.getPlugin() == null || survey.getPlugin().getName().isEmpty()) {
                logger.debug("No plugin set for {} star survey, skipping", survey.getName());
                continue;
            }
            Map<String, String> params = ParameterUtils.resolveParametersForSurvey(survey, resolverResult);
            futures.add(run(survey.getPlugin(), params)
                    .thenAccept(data -> {
                        logger.debug("Found {} entries from {} star survey", data.size(), survey.getName());
                        if (!data.isEmpty()) {
                            resultMap.put(survey, data);
                            if (onResultsFound != null)
                                onResultsFound.accept(survey);
                        } else {
                            if (onNoResultsFound != null)
                                onNoResultsFound.accept(survey);
                        }
                    }));
        }
        futures.forEach(future -> {
            try {
                future.get();
            } catch (InterruptedException e) {
                logger.debug("Task got cancelled.");
            } catch (ExecutionException e) {
                throw new RuntimeException("Failed to wait for result for all star survey tasks.", e);
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
        logger.debug("Running plugin {}", plugin.getName());
        ProcessStarter<PhotometricData> starter = new PhotometricDataProcessStarter();
        if (!starter.prepare(plugin.getCommands(), params)) {
            logger.debug("Not able to prepare {} plugin command", plugin.getName());
            return CompletableFuture.completedFuture(new ArrayList<>());
        }
        processStarters.add(starter);
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

    @Override
    public void cancelAll() {
        logger.debug("Cancelling all plugin tasks");
        if (futures != null) {
            futures.stream().filter(future -> future != null && !future.isDone()).forEach(future -> {
                future.cancel(true);
            });
        }
        if (processStarters != null) {
            processStarters.stream().filter(p -> p != null).forEach(ProcessStarter::cancel);
        }
    }
}
