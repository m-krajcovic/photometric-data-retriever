package cz.muni.physics.pdr.javafx.service;

import cz.muni.physics.pdr.entity.PhotometricData;
import cz.muni.physics.pdr.entity.StarSurvey;
import cz.muni.physics.pdr.manager.StarSurveyManager;
import cz.muni.physics.pdr.model.PhotometricDataModel;
import cz.muni.physics.pdr.plugin.PluginStarter;
import cz.muni.physics.pdr.resolver.StarResolverResult;
import cz.muni.physics.pdr.utils.ParameterUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * @author Michal Krajčovič
 * @version 1.0
 * @since 06/04/16
 */
@Component
public class StarSurveySearchService extends Service<Map<StarSurvey, List<PhotometricDataModel>>> {

    private final static Logger logger = LogManager.getLogger(StarSurveySearchService.class);

    @Autowired
    private PluginStarter<PhotometricData> pluginStarter;
    @Autowired
    private StarSurveyManager starSurveyManager;

    private StarResolverResult nameResolverResult;
    private List<StarSurvey> starSurveys;
    private ObservableMap<StarSurvey, Boolean> starSurveysMap = FXCollections.observableMap(new HashMap<>());

    @Override
    public void reset() {
        super.reset();
        starSurveysMap.clear();
    }

    @PostConstruct
    private void initialize() {
        starSurveys = new ArrayList<>(starSurveyManager.getAll());
    }

    @Override
    public Task<Map<StarSurvey, List<PhotometricDataModel>>> createTask() {
        if (nameResolverResult == null) {
            throw new IllegalArgumentException("nameResolverResult is null.");
        }
        if (starSurveys == null) {
            throw new IllegalArgumentException("starSurveys is null.");
        }
        if (starSurveysMap == null) {
            throw new IllegalArgumentException("starSurveysMap is null.");
        }
        return new Task<Map<StarSurvey, List<PhotometricDataModel>>>() {
            @Override
            protected Map<StarSurvey, List<PhotometricDataModel>> call() {
                Map<StarSurvey, List<PhotometricDataModel>> resultMap = Collections.synchronizedMap(new HashMap<>());
                List<Future> futures = new ArrayList<>();
                for (StarSurvey survey : starSurveys) {
                    if (survey.getPlugin() == null || survey.getPlugin().getName().isEmpty()) {
                        logger.debug("No plugin set for {} star survey, skipping", survey.getName());
                        continue;
                    }
                    Map<String, String> params = ParameterUtils.resolveParametersForSurvey(survey, nameResolverResult);
                    logger.debug("Starting task for {}", survey.getName());
                    futures.add(pluginStarter.run(survey.getPlugin(), params)
                            .thenAccept(data -> {
                                logger.debug("Found {} entries from {} star survey", data.size(), survey.getName());
                                if (!data.isEmpty()) {
                                    List<PhotometricDataModel> models = new ArrayList<>();
                                    data.forEach(d -> models.add(new PhotometricDataModel(d)));
                                    resultMap.put(survey, models);
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

    public StarResolverResult getNameResolverResult() {
        return nameResolverResult;
    }

    public void setNameResolverResult(StarResolverResult nameResolverResult) {
        this.nameResolverResult = nameResolverResult;
    }

    public ObservableMap<StarSurvey, Boolean> getStarSurveysMap() {
        return starSurveysMap;
    }

    public void setStarSurveysMap(ObservableMap<StarSurvey, Boolean> starSurveysMap) {
        this.starSurveysMap = starSurveysMap;
    }
}
