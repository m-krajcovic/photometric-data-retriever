package cz.muni.physics.pdr.backend.resolver.simbad;

import cz.muni.physics.pdr.backend.entity.Radius;
import cz.muni.physics.pdr.backend.resolver.AvailabilityQueryable;

import java.util.List;

/**
 * @author Michal
 * @version 1.0
 * @since 8/10/2016
 */
public interface SimbadResolver extends AvailabilityQueryable {
    List<SimbadResult> findByCoords(String query, Radius radius);
    SimbadResult findByIdentifier(String query);
}
