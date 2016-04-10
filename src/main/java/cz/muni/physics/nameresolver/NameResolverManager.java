package cz.muni.physics.nameresolver;

import org.springframework.cache.annotation.Cacheable;

import java.util.Map;

/**
 * @author Michal Krajčovič
 * @version 1.0
 * @since 10/04/16
 */
public interface NameResolverManager {
    @Cacheable
    NameResolverResult resolveFor(String name);

    Map<NameResolver, Boolean> getAvailableNameResolvers();
}
