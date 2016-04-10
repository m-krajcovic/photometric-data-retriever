package cz.muni.physics.pdr.nameresolver;

import org.springframework.beans.factory.annotation.Autowired;
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
public class NameResolverManagerImpl implements NameResolverManager {

    @Autowired
    private Set<NameResolver> nameResolvers;

    @Override
    public NameResolverResult resolveFor(String name) {
        NameResolverResult result = new NameResolverResult();
        for (NameResolver nameResolver : nameResolvers) {
            result.merge(nameResolver.getResult(name));
        }
        return result;
    }

    @Override
    public Map<NameResolver, Boolean> getAvailableNameResolvers() {
        Map<NameResolver, Boolean> result = new HashMap<>();
        for (NameResolver resolver : nameResolvers) {
            result.put(resolver, resolver.isAvailable());
        }
        return result;
    }
}
