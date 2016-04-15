package cz.muni.physics.pdr.backend.resolver.coords;

import cz.muni.physics.pdr.backend.resolver.StarCoordinates;
import cz.muni.physics.pdr.backend.resolver.StarResolver;
import cz.muni.physics.pdr.backend.resolver.StarResolverManager;
import cz.muni.physics.pdr.backend.resolver.StarResolverResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Michal Krajčovič
 * @version 1.0
 * @since 13/04/16
 */
@Component
public class CoordsResolverManager implements StarResolverManager<StarCoordinates> {

    @Autowired
    private Set<StarResolver<StarCoordinates>> coordsResolvers;

    @Override
    @Cacheable
    public StarResolverResult resolveFor(StarCoordinates name) {
        StarResolverResult result = new StarResolverResult();
        for (StarResolver<StarCoordinates> nameResolver : coordsResolvers) {
            result.merge(nameResolver.getResult(name));
        }
        return result;
    }

    @Override
    public Map<StarResolver<StarCoordinates>, Boolean> getAvailableStarResolvers() {
        Map<StarResolver<StarCoordinates>, Boolean> result = new HashMap<>();
        for (StarResolver<StarCoordinates> resolver : coordsResolvers) {
            result.put(resolver, resolver.isAvailable());
        }
        return result;
    }
}
