package cz.muni.physics.pdr.resolver.coords;

import cz.muni.physics.pdr.resolver.StarCoordinates;
import cz.muni.physics.pdr.resolver.StarResolver;
import cz.muni.physics.pdr.resolver.StarResolverResult;

/**
 * @author Michal Krajčovič
 * @version 1.0
 * @since 13/04/16
 */
public class CoordsResolver implements StarResolver<StarCoordinates>  {
    @Override
    public StarResolverResult getResult(StarCoordinates param) {
        return null;
    }

    @Override
    public boolean isAvailable() {
        return false;
    }
}
