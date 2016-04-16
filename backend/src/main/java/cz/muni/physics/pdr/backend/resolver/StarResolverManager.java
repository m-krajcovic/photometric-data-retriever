package cz.muni.physics.pdr.backend.resolver;

import cz.muni.physics.pdr.backend.entity.StellarObject;

import java.util.Map;

/**
 * @author Michal Krajčovič
 * @version 1.0
 * @since 13/04/16
 */
public interface StarResolverManager<T> {
    StellarObject resolveFor(T param);

    Map<StarResolver<T>, Boolean> getAvailableStarResolvers();
}
