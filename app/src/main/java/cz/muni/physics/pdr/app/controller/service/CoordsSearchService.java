package cz.muni.physics.pdr.app.controller.service;

import cz.muni.physics.pdr.app.model.StellarObjectModel;
import cz.muni.physics.pdr.backend.entity.CelestialCoordinates;
import cz.muni.physics.pdr.backend.entity.StellarObject;
import cz.muni.physics.pdr.backend.resolver.StarResolverManager;
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

    private StarResolverManager<CelestialCoordinates> coordsResolverManager;

    private CelestialCoordinates coords;

    @Autowired
    public CoordsSearchService(StarResolverManager<CelestialCoordinates> coordsResolverManager) {
        this.coordsResolverManager = coordsResolverManager;
    }

    @Override
    protected Task<List<StellarObjectModel>> createTask() {
        if (coords == null) {
            throw new IllegalArgumentException("coords cannot be null.");
        }

        return new Task<List<StellarObjectModel>>() {
            @Override
            protected List<StellarObjectModel> call() throws Exception {
                List<StellarObject> results = coordsResolverManager.resolverForResults(coords);
                List<StellarObjectModel> models = new ArrayList<>(results.size());
                models.addAll(results.stream().map(StellarObjectModel::new).collect(Collectors.toList()));
                return models;
            }
        };
    }

    @Autowired
    public void setTaskExecutor(Executor executor){
        super.setExecutor(executor);
    }

    public CelestialCoordinates getCoords() {
        return coords;
    }

    public void setCoords(CelestialCoordinates coords) {
        this.coords = coords;
    }
}
