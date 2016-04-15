package cz.muni.physics.pdr.backend.resolver.name;

import cz.muni.physics.pdr.backend.resolver.StarName;
import cz.muni.physics.pdr.backend.resolver.StarResolver;
import cz.muni.physics.pdr.backend.resolver.StarResolverManager;
import cz.muni.physics.pdr.backend.resolver.StarResolverResult;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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

    private final static Logger logger = LogManager.getLogger(NameResolverManager.class);

    @Autowired
    private Set<StarResolver<StarName>> nameResolvers;

    @Override
    @Cacheable
    public StarResolverResult resolveFor(StarName name) {
        StarResolverResult result = new StarResolverResult();
        for (StarResolver<StarName> nameResolver : nameResolvers) {
            logger.debug("Resolving star data from name {} by Resolver {}.", name.getValue(), nameResolver.getClass().getCanonicalName());
            result.merge(nameResolver.getResult(name));
        }
        return result;
    }

    @Override
    public Map<StarResolver<StarName>, Boolean> getAvailableStarResolvers() {
        Map<StarResolver<StarName>, Boolean> result = new HashMap<>();
        for (StarResolver<StarName> resolver : nameResolvers) {
            boolean isAvailable = resolver.isAvailable();
            logger.debug("Resolver {} is {} available.", resolver.getClass().getCanonicalName(), isAvailable ? "" : "not");
            result.put(resolver, isAvailable);
        }
        return result;
    }
}
