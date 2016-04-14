package cz.muni.physics.pdr.resolver;

import java.util.Map;

/**
 * @author Michal Krajčovič
 * @version 1.0
 * @since 13/04/16
 */
public interface StarResolverManager<T> {
    StarResolverResult resolveFor(T param);

    Map<StarResolver<T>, Boolean> getAvailableStarResolvers();
}
