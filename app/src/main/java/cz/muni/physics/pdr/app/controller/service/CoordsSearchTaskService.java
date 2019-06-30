package cz.muni.physics.pdr.app.controller.service;

import cz.muni.physics.pdr.app.model.SearchModel;
import cz.muni.physics.pdr.app.model.StellarObjectModel;
import cz.muni.physics.pdr.app.spring.Screens;
import cz.muni.physics.pdr.backend.CosmicCoordinates;
import cz.muni.physics.pdr.backend.entity.Radius;
import cz.muni.physics.pdr.backend.entity.StellarObject;
import cz.muni.physics.pdr.backend.entity.VizierQuery;
import cz.muni.physics.pdr.backend.exception.ResourceAvailabilityException;
import cz.muni.physics.pdr.backend.resolver.simbad.SimbadResolver;
import cz.muni.physics.pdr.backend.resolver.simbad.SimbadResult;
import cz.muni.physics.pdr.backend.resolver.vizier.DistanceModel;
import cz.muni.physics.pdr.backend.resolver.vizier.VariableStarInformationModel;
import cz.muni.physics.pdr.backend.resolver.vizier.VsxResolver;
import cz.muni.physics.pdr.backend.utils.NumberUtils;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * JavaFX concurrent service class for searching for stellar objects by given coordinates
 *
 * @author Michal Krajčovič
 * @version 1.0
 * @since 16/04/16
 */
@Component
public class CoordsSearchTaskService extends Service<Map<String, List<StellarObjectModel>>> {
    private static final Logger logger = LogManager.getLogger(CoordsSearchTaskService.class);

    private Screens app;
    private VsxResolver vsxResolver;
    private SimbadResolver simbadResolver;
    private SearchModel searchModel;
    private Callback onDone;
    private Consumer<String> onError;
    private NameSearchTaskService nameSearchTaskService;
    private ResourceBundle resources;

    @Autowired
    public CoordsSearchTaskService(Screens app,
                                   VsxResolver vsxResolver,
                                   SimbadResolver simbadResolver,
                                   NameSearchTaskService nameSearchTaskService) {
        this.app = app;
        this.vsxResolver = vsxResolver;
        this.nameSearchTaskService = nameSearchTaskService;
        this.simbadResolver = simbadResolver;
    }

    @Override
    protected Task<Map<String, List<StellarObjectModel>>> createTask() {
        if (searchModel == null) {
            throw new IllegalArgumentException("search model cannot be null.");
        }

        return new Task<Map<String, List<StellarObjectModel>>>() {
            @Override
            protected Map<String, List<StellarObjectModel>> call() throws ResourceAvailabilityException {
                Map<String, List<StellarObjectModel>> result = new HashMap<>();

                result.put("VSX", getVsxResults());
                result.put("Simbad", getSimbadResults());

                return result;
            }
        };
    }

    private List<StellarObjectModel> getSimbadResults() {
        List<SimbadResult> simbadResults = simbadResolver.findByCoords(searchModel.getQuery(), new Radius(searchModel.getRadius().getRadius(), searchModel.getRadius().getUnit().toBackend()));
        List<StellarObjectModel> result = new ArrayList<>(simbadResults.size());
        result.addAll(simbadResults.stream().map(r -> {
            StellarObjectModel model = new StellarObjectModel();
            model.setDistance(r.getDistance());
            model.setName(r.getIdentifier());
            model.setRightAscension(r.getRightAscension());
            model.setDeclination(r.getDeclination());
            return model;
        }).collect(Collectors.toList()));
        return result;
    }

    private List<StellarObjectModel> getVsxResults() {
        List<DistanceModel<VariableStarInformationModel>> vsxResults = vsxResolver.findByCoords(new CosmicCoordinates(searchModel.getQuery()), searchModel.getRadius().toDegrees());
        return vsxResults.stream().map(r -> new StellarObjectModel(
                r.getModel().getOriginalName(),
                r.getModel().getCoordinates().getRightAscension(),
                r.getModel().getCoordinates().getDeclination(),
                r.getDistance(),
                r.getModel().getM0().doubleValue(),
                r.getModel().getPeriod().doubleValue()
        )).collect(Collectors.toList());
    }

    @Override
    public void reset() {
        super.reset();
    }

    @Override
    protected void succeeded() {
        logger.debug("CoordsSearchService succeeded");
        Map<String, List<StellarObjectModel>> searchResult = getValue();
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
                obj.setEpoch(selected.getEpoch());
                obj.setPeriod(selected.getPeriod());
                if (NumberUtils.isParsable(selected.getRightAscension())) {
                    obj.setRightAscension(Double.parseDouble(selected.getRightAscension()));
                }
                if (NumberUtils.isParsable(selected.getDeclination())) {
                    obj.setDeclination(Double.parseDouble(selected.getDeclination()));
                }
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
        return new VizierQuery(searchModel.getQuery(), new Radius(searchModel.getRadius().getRadius(), searchModel.getRadius().getUnit().toBackend()));
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
