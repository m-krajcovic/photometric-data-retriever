package cz.muni.physics.pdr.backend.resolver;

/**
 * @author Michal Krajčovič
 * @version 1.0
 * @since 13/04/16
 */
public interface StarResolver<T> {
    StarResolverResult getResult(T param);

    boolean isAvailable();
}
