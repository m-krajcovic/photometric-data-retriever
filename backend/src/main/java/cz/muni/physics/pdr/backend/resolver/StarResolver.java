package cz.muni.physics.pdr.backend.resolver;

import cz.muni.physics.pdr.backend.entity.StellarObject;

import java.util.List;

/**
 * @author Michal Krajčovič
 * @version 1.0
 * @since 13/04/16
 */
public interface StarResolver<T> {

    List<StellarObject> getResults(T param);

    StellarObject getResult(T param);

    boolean isAvailable();
}
