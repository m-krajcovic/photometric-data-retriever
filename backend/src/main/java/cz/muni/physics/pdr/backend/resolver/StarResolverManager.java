package cz.muni.physics.pdr.backend.resolver;

import cz.muni.physics.pdr.backend.entity.StellarObject;

import java.util.List;
import java.util.Map;

/**
 * @author Michal Krajčovič
 * @version 1.0
 * @since 13/04/16
 */
public interface StarResolverManager<T> {
    StellarObject resolverForResult(T param);

    List<StellarObject> resolverForResults(T param);

    Map<StarResolver<T>, Boolean> getAvailableStarResolvers();
}
