package cz.muni.physics.pdr.backend.resolver.coords;

import cz.muni.physics.pdr.backend.resolver.StarCoordinates;
import cz.muni.physics.pdr.backend.resolver.StarResolver;
import cz.muni.physics.pdr.backend.resolver.StarResolverManager;
import cz.muni.physics.pdr.backend.resolver.StarResolverResult;

import java.util.Map;

/**
 * @author Michal Krajčovič
 * @version 1.0
 * @since 13/04/16
 */
public class CoordsResolverManager implements StarResolverManager<StarCoordinates> {

    @Override
    public StarResolverResult resolveFor(StarCoordinates param) {
        return null;
    }

    @Override
    public Map<StarResolver<StarCoordinates>, Boolean> getAvailableStarResolvers() {
        return null;
    }
}
