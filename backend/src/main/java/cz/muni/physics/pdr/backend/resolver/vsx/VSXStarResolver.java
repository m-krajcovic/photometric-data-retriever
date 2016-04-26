package cz.muni.physics.pdr.backend.resolver.vsx;

import cz.muni.physics.pdr.backend.entity.CelestialCoordinates;
import cz.muni.physics.pdr.backend.entity.StellarObject;

import java.util.List;

/**
 * @author Michal Krajčovič
 * @version 1.0
 * @since 19/04/16
 */
public interface VSXStarResolver {
    List<StellarObject> findByName(String name);

    List<StellarObject> findByCoordinates(CelestialCoordinates coordinates);

    boolean isAvailable();
}
