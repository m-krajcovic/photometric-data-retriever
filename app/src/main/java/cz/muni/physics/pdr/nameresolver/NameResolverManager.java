package cz.muni.physics.pdr.nameresolver;

import java.util.Map;

/**
 * @author Michal Krajčovič
 * @version 1.0
 * @since 10/04/16
 */
public interface NameResolverManager {
    NameResolverResult resolveFor(String name);

    Map<NameResolver, Boolean> getAvailableNameResolvers();
}
