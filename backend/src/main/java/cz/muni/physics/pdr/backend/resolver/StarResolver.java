package cz.muni.physics.pdr.backend.resolver;

import java.util.List;

/**
 * @author Michal Krajčovič
 * @version 1.0
 * @since 13/04/16
 */
public interface StarResolver<T> {

    List<StarResolverResult> getResults(T param);

    StarResolverResult getResult(T param);

    boolean isAvailable();
}
