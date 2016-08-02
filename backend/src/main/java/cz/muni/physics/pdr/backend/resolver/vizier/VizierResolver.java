package cz.muni.physics.pdr.backend.resolver.vizier;

import cz.muni.physics.pdr.backend.entity.VizierQuery;
import cz.muni.physics.pdr.backend.entity.VizierResult;
import cz.muni.physics.pdr.backend.resolver.AvailabilityQueryable;

import java.util.List;

/**
 * Class for retrieving data from Vizier Service
 *
 * @author Michal
 * @version 1.0
 * @since 28-Apr-16
 */
public interface VizierResolver extends AvailabilityQueryable {
    /**
     * Method to search in Vizier by given query
     *
     * @param query search query for vizier
     * @return list of results from Vizier service
     */
    List<VizierResult> findByQuery(VizierQuery query);

}
