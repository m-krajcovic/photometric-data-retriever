package cz.muni.physics.pdr.backend.resolver.name;

import cz.muni.physics.pdr.backend.entity.StellarObject;
import cz.muni.physics.pdr.backend.entity.StellarObjectName;
import cz.muni.physics.pdr.backend.resolver.StarResolver;
import cz.muni.physics.pdr.backend.resolver.StarResolverManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Michal Krajčovič
 * @version 1.0
 * @since 10/04/16
 */
@Component
public class NameResolverManager implements StarResolverManager<StellarObjectName> {

    private final static Logger logger = LogManager.getLogger(NameResolverManager.class);

    @Autowired
    private Set<StarResolver<StellarObjectName>> nameResolvers;

    @Override
    @Cacheable
    public StellarObject resolverForResult(StellarObjectName name) {
        StellarObject result = new StellarObject();
        for (StarResolver<StellarObjectName> nameResolver : nameResolvers) {
            logger.debug("Resolving star data from name {} by Resolver {}.", name.getValue(), nameResolver.getClass().getCanonicalName());
            result.merge(nameResolver.getResult(name));
        }
        return result;
    }

    @Override
    public List<StellarObject> resolverForResults(StellarObjectName param) {
        List<StellarObject> result = new ArrayList<>();
        for (StarResolver<StellarObjectName> nameResolver : nameResolvers) {
            result.addAll(nameResolver.getResults(param));
        }
        return result;
    }

    @Override
    public Map<StarResolver<StellarObjectName>, Boolean> getAvailableStarResolvers() {
        Map<StarResolver<StellarObjectName>, Boolean> result = new HashMap<>();
        for (StarResolver<StellarObjectName> resolver : nameResolvers) {
            boolean isAvailable = resolver.isAvailable();
            logger.debug("Resolver {} is {} available.", resolver.getClass().getCanonicalName(), isAvailable ? "" : "not");
            result.put(resolver, isAvailable);
        }
        return result;
    }
}
