package cz.muni.physics.pdr.backend.resolver.coords;

import cz.muni.physics.pdr.backend.entity.CelestialCoordinates;
import cz.muni.physics.pdr.backend.resolver.StarResolver;
import cz.muni.physics.pdr.backend.resolver.StarResolverManager;
import cz.muni.physics.pdr.backend.entity.StellarObject;
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
public class CoordsResolverManager implements StarResolverManager<CelestialCoordinates> {

    @Autowired
    private Set<StarResolver<CelestialCoordinates>> coordsResolvers;

    @Override
    @Cacheable
    public StellarObject resolveFor(CelestialCoordinates name) {
        StellarObject result = new StellarObject();
        for (StarResolver<CelestialCoordinates> nameResolver : coordsResolvers) {
            result.merge(nameResolver.getResult(name));
        }
        return result;
    }

    @Override
    public Map<StarResolver<CelestialCoordinates>, Boolean> getAvailableStarResolvers() {
        Map<StarResolver<CelestialCoordinates>, Boolean> result = new HashMap<>();
        for (StarResolver<CelestialCoordinates> resolver : coordsResolvers) {
            result.put(resolver, resolver.isAvailable());
        }
        return result;
    }
}
