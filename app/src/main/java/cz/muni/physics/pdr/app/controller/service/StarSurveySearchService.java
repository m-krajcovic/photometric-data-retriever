package cz.muni.physics.pdr.app.controller.service;

import cz.muni.physics.pdr.app.model.PhotometricDataModel;
import cz.muni.physics.pdr.app.model.StarSurveyModel;
import cz.muni.physics.pdr.backend.entity.StellarObject;
import cz.muni.physics.pdr.backend.exception.ResourceAvailabilityException;
import cz.muni.physics.pdr.backend.resolver.plugin.PhotometricDataRetrieverManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;

/**
 * @author Michal Krajčovič
 * @version 1.0
 * @since 06/04/16
 */
@Component
public class StarSurveySearchService extends Service<Map<StarSurveyModel, List<PhotometricDataModel>>> {

    private final static Logger logger = LogManager.getLogger(StarSurveySearchService.class);

    private PhotometricDataRetrieverManager retrieverManager;

    private StellarObject resolverResult;
    private ObservableMap<StarSurveyModel, Boolean> starSurveysMap = FXCollections.observableMap(new HashMap<>());

    @Autowired
    public StarSurveySearchService(PhotometricDataRetrieverManager pluginStarter) {
        this.retrieverManager = pluginStarter;
        super.setOnCancelled(event -> {
            pluginStarter.cancelAll();
        });
    }

    @Override
    public void reset() {
        super.reset();
        resolverResult = new StellarObject();
        starSurveysMap.clear();
    }

    @Override
    public Task<Map<StarSurveyModel, List<PhotometricDataModel>>> createTask() {
        if (resolverResult == null) {
            throw new IllegalArgumentException("resolverResult is null.");
        }
        if (starSurveysMap == null) {
            throw new IllegalArgumentException("starSurveysMap is null.");
        }
        return new Task<Map<StarSurveyModel, List<PhotometricDataModel>>>() {
            @Override
            protected Map<StarSurveyModel, List<PhotometricDataModel>> call() throws ResourceAvailabilityException {
                logger.debug("Starting task.");
                retrieverManager.setOnNoResultsFound(s -> {
                    starSurveysMap.put(new StarSurveyModel(s), false);
                });
                retrieverManager.setOnResultsFound(s -> {
                    starSurveysMap.put(new StarSurveyModel(s), true);
                });
                Map<StarSurveyModel, List<PhotometricDataModel>> resultMap = new HashMap<>();
                retrieverManager.runAll(resolverResult).forEach((starSurvey, photometricDatas) -> {
                    List<PhotometricDataModel> list = new ArrayList<>();
                    photometricDatas.forEach(d -> list.add(new PhotometricDataModel(d)));
                    resultMap.put(new StarSurveyModel(starSurvey), list);
                });
                return resultMap;
            }
        };
    }

    @Autowired
    public void setTaskExecutor(Executor executor) {
        super.setExecutor(executor);
    }

    public StellarObject getResolverResult() {
        return resolverResult;
    }

    public void setResolverResult(StellarObject resolverResult) {
        this.resolverResult = resolverResult;
    }

    public ObservableMap<StarSurveyModel, Boolean> getStarSurveysMap() {
        return starSurveysMap;
    }

    public void setStarSurveysMap(ObservableMap<StarSurveyModel, Boolean> starSurveysMap) {
        this.starSurveysMap = starSurveysMap;
    }
}
