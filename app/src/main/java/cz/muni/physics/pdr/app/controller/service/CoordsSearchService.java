package cz.muni.physics.pdr.app.controller.service;

import cz.muni.physics.pdr.app.model.StellarObjectModel;
import cz.muni.physics.pdr.backend.entity.VizierQuery;
import cz.muni.physics.pdr.backend.entity.VizierResult;
import cz.muni.physics.pdr.backend.exception.ResourceAvailabilityException;
import cz.muni.physics.pdr.backend.resolver.vizier.VizierResolver;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
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

    private VizierResolver vsxVizierResolver;

    private VizierQuery query;

    @Autowired
    public CoordsSearchService(VizierResolver vsxVizierResolver) {
        this.vsxVizierResolver = vsxVizierResolver;
    }

    @Override
    protected Task<List<StellarObjectModel>> createTask() {
        if (query == null) {
            throw new IllegalArgumentException("coords cannot be null.");
        }

        return new Task<List<StellarObjectModel>>() {
            @Override
            protected List<StellarObjectModel> call() throws ResourceAvailabilityException {
                List<VizierResult> results = vsxVizierResolver.findByQuery(query);
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

    public void setQuery(VizierQuery query) {
        this.query = query;
    }
}
