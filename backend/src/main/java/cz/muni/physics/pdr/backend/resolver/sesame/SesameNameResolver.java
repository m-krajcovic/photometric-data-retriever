package cz.muni.physics.pdr.backend.resolver.sesame;

import cz.muni.physics.pdr.backend.entity.StellarObject;

/**
 * @author Michal Krajčovič
 * @version 1.0
 * @since 19/04/16
 */
public interface SesameNameResolver{
    StellarObject findByName(String name);

    boolean isAvailable();
}
