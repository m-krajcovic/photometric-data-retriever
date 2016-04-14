package cz.muni.physics.pdr.backend.resolver.name;

import cz.muni.physics.pdr.backend.resolver.StarName;
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
 * @since 10/04/16
 */
@Component
public class NameResolverManager implements StarResolverManager<StarName> {

    @Autowired
    private Set<StarResolver<StarName>> nameResolvers;

    @Override
    @Cacheable
    public StarResolverResult resolveFor(StarName name) {
        StarResolverResult result = new StarResolverResult();
        for (StarResolver<StarName> nameResolver : nameResolvers) {
            result.merge(nameResolver.getResult(name));
        }
        return result;
    }

    @Override
    public Map<StarResolver<StarName>, Boolean> getAvailableStarResolvers() {
        Map<StarResolver<StarName>, Boolean> result = new HashMap<>();
        for (StarResolver<StarName> resolver : nameResolvers) {
            result.put(resolver, resolver.isAvailable());
        }
        return result;
    }
}
