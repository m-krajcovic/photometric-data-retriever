package cz.muni.physics.pdr.app.controller.service;

import cz.muni.physics.pdr.app.model.SearchModel;
import cz.muni.physics.pdr.backend.entity.StellarObject;
import cz.muni.physics.pdr.backend.entity.VizierQuery;
import cz.muni.physics.pdr.backend.entity.VizierResult;
import cz.muni.physics.pdr.backend.exception.ResourceAvailabilityException;
import cz.muni.physics.pdr.backend.resolver.sesame.SesameNameResolver;
import cz.muni.physics.pdr.backend.resolver.vizier.VizierResolver;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.Executor;
import java.util.function.Consumer;

/**
 * JavaFX concurrent service class for retrieving Stellar Object information by its name
 * @author Michal Krajčovič
 * @version 1.0
 * @since 06/04/16
 */
@Component
public class NameWithRadiusSearchTaskService extends Service<StellarObject> {
    private static final Logger logger = LogManager.getLogger(NameWithRadiusSearchTaskService.class);
    protected SearchModel searchModel;
    protected SesameNameResolver sesameNameResolver;
    protected VizierResolver vsxVizierResolver;
    protected Callback onDone;
    protected Consumer<String> onError;
    protected StarSurveySearchTaskService starSurveySearchTaskService;
    protected ResourceBundle resources;
    protected StellarObject object = new StellarObject();
    private CoordsSearchTaskService coordsSearchService;

    @Autowired
    public NameWithRadiusSearchTaskService(CoordsSearchTaskService coordsSearchService,SesameNameResolver sesameNameResolver, VizierResolver vsxVizierResolver, StarSurveySearchTaskService starSurveySearchTaskService) {
        this.sesameNameResolver = sesameNameResolver;
        this.vsxVizierResolver = vsxVizierResolver;
        this.starSurveySearchTaskService = starSurveySearchTaskService;
        this.coordsSearchService = coordsSearchService;
    }

    @Override
    protected Task<StellarObject> createTask() {
        if (searchModel == null) {
            throw new IllegalArgumentException("search model cannot be null.");
        }
        return new Task<StellarObject>() {
            @Override
            protected StellarObject call() throws ResourceAvailabilityException {
                logger.debug("Trying to get StellarObject.");
                object.merge(sesameNameResolver.findByName(searchModel.getQuery()));
                List<VizierResult> vsxResult = vsxVizierResolver.findByQuery(new VizierQuery(searchModel.getQuery()));
                if (vsxResult.size() >= 1) {
                    object.setoName(vsxResult.get(0).getName());
                    object.setEpoch(vsxResult.get(0).getEpoch());
                    object.setPeriod(vsxResult.get(0).getPeriod());
                }
                return object.getoName() != null ? object : null;
            }
        };
    }

    @Override
    protected void succeeded() {
        logger.debug("Succeeded in retrieving star resolver data.");
        StellarObject result = getValue();
        if (result != null) {
            searchModel.setQuery(result.getRightAscension() + " " + result.getDeclination());
            coordsSearchService.start();
        } else {
            if (onError != null)
                onError.accept(resources.getString("no.results.found"));
            if (onDone != null) {
                onDone.call();
            }
        }
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

    @Override
    public void reset() {
        super.reset();
        object = new StellarObject();
    }

    public StellarObject getObject() {
        return object;
    }

    public void setObject(StellarObject object) {
        this.object = object;
    }

    public Consumer<String> getOnError() {
        return onError;
    }

    public void setOnError(Consumer<String> onError) {
        this.onError = onError;
    }

    public Callback getOnDone() {
        return onDone;
    }

    public void setOnDone(Callback onDone) {
        this.onDone = onDone;
    }

    public void setModel(SearchModel model) {
        this.searchModel = model;
    }

    @Autowired
    public void setResources(ResourceBundle resources) {
        this.resources = resources;
    }

    @Autowired
    public void setTaskExecutor(Executor executor) {
        super.setExecutor(executor);
    }
}