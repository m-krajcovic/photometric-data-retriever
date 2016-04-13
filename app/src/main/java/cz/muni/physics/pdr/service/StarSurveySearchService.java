package cz.muni.physics.pdr.service;

import cz.muni.physics.pdr.model.PhotometricData;
import cz.muni.physics.pdr.model.StarSurvey;
import cz.muni.physics.pdr.nameresolver.NameResolverResult;
import cz.muni.physics.pdr.plugin.PluginManager;
import cz.muni.physics.pdr.utils.ParameterUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 *
 * @author Michal Krajčovič
 * @version 1.0
 * @since 06/04/16
 */
public class StarSurveySearchService extends Service<Map<StarSurvey, List<PhotometricData>>> {

    private final static Logger logger = LogManager.getLogger(StarSurveySearchService.class);

    @Autowired
    private PluginManager<PhotometricData> pluginManager;

    private NameResolverResult nameResolverResult;
    private List<StarSurvey> starSurveys;
    private ObservableMap<StarSurvey, Boolean> starSurveysMap = FXCollections.observableMap(new HashMap<>());

    @Override
    public void reset() {
        super.reset();
        starSurveysMap.clear();
    }

    @Override
    public Task<Map<StarSurvey, List<PhotometricData>>> createTask() {
        if (nameResolverResult == null) {
            throw new IllegalArgumentException("nameResolverResult is null.");
        }
        if (starSurveys == null) {
            throw new IllegalArgumentException("starSurveys is null.");
        }
        if (starSurveysMap == null) {
            throw new IllegalArgumentException("starSurveysMap is null.");
        }
        return new Task<Map<StarSurvey, List<PhotometricData>>>() {
            @Override
            protected Map<StarSurvey, List<PhotometricData>> call() {
                Map<StarSurvey, List<PhotometricData>> resultMap = Collections.synchronizedMap(new HashMap<>());
                List<Future> futures = new ArrayList<>();
                for (StarSurvey survey : starSurveys) {
                    if (survey.getPlugin() == null || survey.getPlugin().getName().isEmpty()) {
                        logger.debug("No plugin set for {} star survey, skipping", survey.getName());
                        continue;
                    }
                    Map<String, String> params = ParameterUtils.resolveParametersForSurvey(survey, nameResolverResult);
                    logger.debug("Starting task for {}", survey.getName());
                    futures.add(pluginManager.run(survey.getPlugin(), params)
                            .thenAccept(data -> {
                                logger.debug("Found {} entries from {} star survey", data.size(), survey.getName());
                                if (!data.isEmpty()) {
                                    resultMap.put(survey, data);
                                    starSurveysMap.put(survey, true);
                                } else {
                                    starSurveysMap.put(survey, false);
                                }
                            }));
                }
                futures.forEach(future -> {
                    try {
                        future.get();
                    } catch (InterruptedException | ExecutionException e) {
                        logger.error("Failed to wait for result for all star survey tasks.", e);
                    }
                });

                return resultMap;
            }
        };
    }

    public NameResolverResult getNameResolverResult() {
        return nameResolverResult;
    }

    public void setNameResolverResult(NameResolverResult nameResolverResult) {
        this.nameResolverResult = nameResolverResult;
    }

    public List<StarSurvey> getStarSurveys() {
        return starSurveys;
    }

    public void setStarSurveys(List<StarSurvey> starSurveys) {
        this.starSurveys = starSurveys;
    }

    public ObservableMap<StarSurvey, Boolean> getStarSurveysMap() {
        return starSurveysMap;
    }

    public void setStarSurveysMap(ObservableMap<StarSurvey, Boolean> starSurveysMap) {
        this.starSurveysMap = starSurveysMap;
    }
}
