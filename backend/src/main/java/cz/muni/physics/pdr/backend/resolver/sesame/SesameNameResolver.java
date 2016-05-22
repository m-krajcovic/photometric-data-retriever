package cz.muni.physics.pdr.backend.resolver.sesame;

import cz.muni.physics.pdr.backend.entity.StellarObject;

/**
 * Class for retrieving data from Sesame Name Resolver web service
 * @author Michal Krajčovič
 * @version 1.0
 * @since 19/04/16
 */
public interface SesameNameResolver {
    /**
     * Method that calls Sesame Name Resolver service and returns StellarObject with aliases and coords from it
     * @param name name to search for
     * @return StellarObject with aliases and coords
     */
    StellarObject findByName(String name);

    /**
     * Checks if Sesame is available
     * @return
     */
    boolean isAvailable();
}
