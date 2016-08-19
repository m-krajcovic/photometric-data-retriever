package cz.muni.physics.pdr.app.controller.service;

import cz.muni.physics.pdr.app.model.PhotometricDataModel;
import cz.muni.physics.pdr.app.model.StarSurveyModel;
import cz.muni.physics.pdr.app.model.StellarObjectModel;
import cz.muni.physics.pdr.app.spring.Screens;
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
import java.util.ResourceBundle;
import java.util.concurrent.Executor;
import java.util.function.Consumer;

/**
 * JavaFX concurrent service class for retrieving PhotometricData for given stellar object
 * @author Michal Krajčovič
 * @version 1.0
 * @since 06/04/16
 */
@Component
public class StarSurveySearchTaskService extends Service<Map<StarSurveyModel, List<PhotometricDataModel>>> {

    private static final Logger logger = LogManager.getLogger(StarSurveySearchTaskService.class);

    private Screens app;
    private PhotometricDataRetrieverManager retrieverManager;
    private StellarObject resolverResult;
    private ObservableMap<StarSurveyModel, Boolean> starSurveysMap = FXCollections.observableMap(new HashMap<>());
    private Callback onDone;
    private Consumer<String> onError;
    private ResourceBundle resources;

    @Autowired
    public StarSurveySearchTaskService(Screens app,
                                       PhotometricDataRetrieverManager retrieverManager) {
        this.app = app;
        this.retrieverManager = retrieverManager;
        super.setOnCancelled(event -> {
            retrieverManager.cancelAll();
        });
        retrieverManager.setOnNoResultsFound(s -> {
            starSurveysMap.put(new StarSurveyModel(s), false);
        });
        retrieverManager.setOnResultsFound(s -> {
            starSurveysMap.put(new StarSurveyModel(s), true);
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

    @Override
    protected void succeeded() {
        Map<StarSurveyModel, List<PhotometricDataModel>> data = getValue();
        if (data.isEmpty() || data.values().stream().allMatch(List::isEmpty)) {
            if (onError != null)
                onError.accept("No results found.");
        } else {
            StellarObjectModel model = new StellarObjectModel(resolverResult.getoName(),
                    resolverResult.getRightAscension(),
                    resolverResult.getDeclination(),
                    resolverResult.getDistance(),
                    resolverResult.getEpoch(),
                    resolverResult.getPeriod());
            app.showPhotometricDataOverview(data, model);
        }

        if (onDone != null)
            onDone.call();
        reset();
    }

    @Override
    protected void failed() {
        logger.error("Failed to finish task", getException());
        if (onError != null)
            onError.accept(resources.getString("error.occured"));
        if (onDone != null) {
            onDone.call();
        }
        reset();
    }

    @Autowired
    public void setTaskExecutor(Executor executor) {
        super.setExecutor(executor);
    }

    @Autowired
    public void setResources(ResourceBundle resources) {
        this.resources = resources;
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

    public Callback getOnDone() {
        return onDone;
    }

    public void setOnDone(Callback onDone) {
        this.onDone = onDone;
    }

    public Consumer<String> getOnError() {
        return onError;
    }

    public void setOnError(Consumer<String> onError) {
        this.onError = onError;
    }
}
