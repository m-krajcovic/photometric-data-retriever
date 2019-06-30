package cz.muni.physics.pdr.app.controller.service;

import cz.muni.physics.pdr.app.model.SearchModel;
import cz.muni.physics.pdr.backend.entity.StellarObject;
import cz.muni.physics.pdr.backend.exception.ResourceAvailabilityException;
import cz.muni.physics.pdr.backend.resolver.sesame.SesameNameResolver;
import cz.muni.physics.pdr.backend.resolver.vizier.VariableStarInformationModel;
import cz.muni.physics.pdr.backend.resolver.vizier.VsxResolver;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.Executor;
import java.util.function.Consumer;

/**
 * JavaFX concurrent service class for retrieving Stellar Object information by its name
 *
 * @author Michal Krajčovič
 * @version 1.0
 * @since 06/04/16
 */
@Component
public class NameWithRadiusSearchTaskService extends Service<StellarObject> {
    private static final Logger logger = LogManager.getLogger(NameWithRadiusSearchTaskService.class);
    protected SearchModel searchModel;
    protected SesameNameResolver sesameNameResolver;
    protected VsxResolver vsxResolver;
    protected Callback onDone;
    protected Consumer<String> onError;
    protected StarSurveySearchTaskService starSurveySearchTaskService;
    protected ResourceBundle resources;
    protected StellarObject object = new StellarObject();
    private CoordsSearchTaskService coordsSearchService;

    @Autowired
    public NameWithRadiusSearchTaskService(CoordsSearchTaskService coordsSearchService, SesameNameResolver sesameNameResolver, VsxResolver vsxResolver, StarSurveySearchTaskService starSurveySearchTaskService) {
        this.sesameNameResolver = sesameNameResolver;
        this.vsxResolver = vsxResolver;
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
                Optional<VariableStarInformationModel> vsxResult = vsxResolver.findByName(searchModel.getQuery());
                vsxResult.ifPresent(result -> object.merge(result));
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
