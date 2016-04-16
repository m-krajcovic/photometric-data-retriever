package cz.muni.physics.pdr.app.controller.service;

import cz.muni.physics.pdr.app.model.StellarObjectModel;
import cz.muni.physics.pdr.backend.entity.CelestialCoordinates;
import cz.muni.physics.pdr.backend.entity.StellarObject;
import cz.muni.physics.pdr.backend.resolver.StarResolver;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Michal Krajčovič
 * @version 1.0
 * @since 16/04/16
 */
@Component
public class CoordsSearchService extends Service<List<StellarObjectModel>> {

    @Autowired
    private StarResolver<CelestialCoordinates> coordsResolver;

    private CelestialCoordinates coords;

    @Override
    protected Task<List<StellarObjectModel>> createTask() {
        if (coords == null) {
            throw new IllegalArgumentException("coords cannot be null.");
        }

        return new Task<List<StellarObjectModel>>() {
            @Override
            protected List<StellarObjectModel> call() throws Exception {
                List<StellarObject> results = coordsResolver.getResults(coords);
                List<StellarObjectModel> models = new ArrayList<>(results.size());
                for (StellarObject result : results) {
                    models.add(new StellarObjectModel(result));
                }
                return models;
            }
        };
    }

    public CelestialCoordinates getCoords() {
        return coords;
    }

    public void setCoords(CelestialCoordinates coords) {
        this.coords = coords;
    }
}
