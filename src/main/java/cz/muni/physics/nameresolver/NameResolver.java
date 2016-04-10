package cz.muni.physics.nameresolver;

/**
 * @author Michal Krajčovič
 * @version 1.0
 * @since 10/04/16
 */
public interface NameResolver {
    NameResolverResult getResult(String name);

    boolean isAvailable();
}
