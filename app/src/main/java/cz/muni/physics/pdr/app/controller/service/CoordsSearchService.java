package cz.muni.physics.pdr.app.controller.service;

import cz.muni.physics.pdr.app.model.SearchModel;
import cz.muni.physics.pdr.app.model.StellarObjectModel;
import cz.muni.physics.pdr.backend.entity.Radius;
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
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

/**
 * @author Michal Krajčovič
 * @version 1.0
 * @since 16/04/16
 */
@Component
public class CoordsSearchService extends Service<List<StellarObjectModel>> {
    private final static Logger logger = LogManager.getLogger(CoordsSearchService.class);

    private VizierResolver vsxVizierResolver;
    private SearchModel searchModel;

    @Autowired
    public CoordsSearchService(VizierResolver vsxVizierResolver) {
        this.vsxVizierResolver = vsxVizierResolver;
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

    @Autowired
    public void setTaskExecutor(Executor executor) {
        super.setExecutor(executor);
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
}
