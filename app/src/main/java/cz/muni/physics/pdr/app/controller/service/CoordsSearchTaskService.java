package cz.muni.physics.pdr.app.controller.service;

import cz.muni.physics.pdr.app.model.SearchModel;
import cz.muni.physics.pdr.app.model.StellarObjectModel;
import cz.muni.physics.pdr.app.spring.Screens;
import cz.muni.physics.pdr.backend.entity.Radius;
import cz.muni.physics.pdr.backend.entity.StellarObject;
import cz.muni.physics.pdr.backend.entity.VizierQuery;
import cz.muni.physics.pdr.backend.entity.VizierResult;
import cz.muni.physics.pdr.backend.exception.ResourceAvailabilityException;
import cz.muni.physics.pdr.backend.resolver.vizier.VizierResolver;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * JavaFX concurrent service class for searching for stellar objects by given coordinates
 * @author Michal Krajčovič
 * @version 1.0
 * @since 16/04/16
 */
@Component
public class CoordsSearchTaskService extends Service<List<StellarObjectModel>> {
    private final static Logger logger = LogManager.getLogger(CoordsSearchTaskService.class);

    private Screens app;
    private VizierResolver vsxVizierResolver;
    private SearchModel searchModel;
    private Callback onDone;
    private Consumer<String> onError;
    private NameSearchTaskService nameSearchTaskService;
    private ResourceBundle resources;

    @Autowired
    public CoordsSearchTaskService(Screens app,
                                   VizierResolver vsxVizierResolver,
                                   NameSearchTaskService nameSearchTaskService) {
        this.app = app;
        this.vsxVizierResolver = vsxVizierResolver;
        this.nameSearchTaskService = nameSearchTaskService;
    }

    @Override
    protected Task<List<StellarObjectModel>> createTask() {
        if (searchModel == null) {
            throw new IllegalArgumentException("search model cannot be null.");
        }

        return new Task<List<StellarObjectModel>>() {
            @Override
            protected List<StellarObjectModel> call() throws ResourceAvailabilityException {
                List<VizierResult> results = vsxVizierResolver.findByQuery(parseModel());
                List<StellarObjectModel> models = new ArrayList<>(results.size());
                models.addAll(results.stream().map(r -> {
                    return new StellarObjectModel(r.getName(),
                            r.getRightAscension(),
                            r.getDeclination(),
                            r.getDistance(),
                            r.getEpoch(),
                            r.getPeriod());
                }).collect(Collectors.toList()));
                return models;
            }
        };
    }

    @Override
    public void reset() {
        super.reset();
    }

    @Override
    protected void succeeded() {
        logger.debug("CoordsSearchService succeeded");
        List<StellarObjectModel> searchResult = getValue();
        if (searchResult.isEmpty()) {
            if (onError != null)
                onError.accept(resources.getString("no.results.found"));
            if (onDone != null) {
                onDone.call();
            }
        } else {
            StellarObjectModel selected = app.showStellarObjects(searchResult);
            if (selected != null) {
                searchModel.setQuery(selected.getName());
                StellarObject obj = new StellarObject();
                obj.setoName(selected.getName());
                obj.setRightAscension(selected.getRightAscension());
                obj.setDeclination(selected.getDeclination());
                obj.setEpoch(selected.getEpoch());
                obj.setPeriod(selected.getPeriod());
                nameSearchTaskService.setObject(obj);
                nameSearchTaskService.start();
            } else {
                if (onDone != null) {
                    onDone.call();
                }
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

    private VizierQuery parseModel() {
        Radius.Unit rUnit;
        switch (searchModel.getRadius().getUnit()) {
            case ARCSEC:
                rUnit = Radius.Unit.ARC_SEC;
                break;
            case ARCMIN:
                rUnit = Radius.Unit.ARC_MIN;
                break;
            default:
                rUnit = Radius.Unit.DEG;
                break;
        }
        return new VizierQuery(searchModel.getQuery(), new Radius(searchModel.getRadius().getRadius(), rUnit));
    }

    public void setModel(SearchModel searchModel) {
        this.searchModel = searchModel;
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

    @Autowired
    public void setResources(ResourceBundle resources) {
        this.resources = resources;
    }

    @Autowired
    public void setTaskExecutor(Executor executor) {
        super.setExecutor(executor);
    }

}
