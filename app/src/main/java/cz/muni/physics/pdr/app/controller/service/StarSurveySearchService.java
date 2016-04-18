package cz.muni.physics.pdr.app.controller.service;

import cz.muni.physics.pdr.app.model.PhotometricDataModel;
import cz.muni.physics.pdr.backend.entity.StarSurvey;
import cz.muni.physics.pdr.backend.entity.StellarObject;
import cz.muni.physics.pdr.backend.plugin.StarSurveyPluginStarter;
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
public class StarSurveySearchService extends Service<Map<StarSurvey, List<PhotometricDataModel>>> {

    private final static Logger logger = LogManager.getLogger(StarSurveySearchService.class);

    private StarSurveyPluginStarter pluginStarter;

    private StellarObject resolverResult;
    private ObservableMap<StarSurvey, Boolean> starSurveysMap = FXCollections.observableMap(new HashMap<>());

    @Autowired
    public StarSurveySearchService(StarSurveyPluginStarter pluginStarter,
                                   Executor executor) {
        this.pluginStarter = pluginStarter;
        super.setExecutor(executor);
    }

    @Override
    public void reset() {
        super.reset();
        resolverResult = new StellarObject();
        starSurveysMap.clear();
    }

    @Override
    public Task<Map<StarSurvey, List<PhotometricDataModel>>> createTask() {
        if (resolverResult == null) {
            throw new IllegalArgumentException("resolverResult is null.");
        }
        if (starSurveysMap == null) {
            throw new IllegalArgumentException("starSurveysMap is null.");
        }
        return new Task<Map<StarSurvey, List<PhotometricDataModel>>>() {
            @Override
            protected Map<StarSurvey, List<PhotometricDataModel>> call() {
                logger.debug("Starting task.");
                pluginStarter.setOnNoResultsFound(s -> starSurveysMap.put(s, false));
                pluginStarter.setOnResultsFound(s -> starSurveysMap.put(s, true));
                Map<StarSurvey, List<PhotometricDataModel>> resultMap = new HashMap<>();
                pluginStarter.runAll(resolverResult).forEach((starSurvey, photometricDatas) -> {
                    List<PhotometricDataModel> list = new ArrayList<>();
                    photometricDatas.forEach(d -> list.add(new PhotometricDataModel(d)));
                    resultMap.put(starSurvey, list);
                });
                return resultMap;
            }
        };
    }

    public StellarObject getResolverResult() {
        return resolverResult;
    }

    public void setResolverResult(StellarObject resolverResult) {
        this.resolverResult = resolverResult;
    }

    public ObservableMap<StarSurvey, Boolean> getStarSurveysMap() {
        return starSurveysMap;
    }

    public void setStarSurveysMap(ObservableMap<StarSurvey, Boolean> starSurveysMap) {
        this.starSurveysMap = starSurveysMap;
    }
}
