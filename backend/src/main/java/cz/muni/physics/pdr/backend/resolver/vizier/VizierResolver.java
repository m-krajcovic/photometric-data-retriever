package cz.muni.physics.pdr.backend.resolver.vizier;

import cz.muni.physics.pdr.backend.entity.VizierQuery;
import cz.muni.physics.pdr.backend.entity.VizierResult;

import java.util.List;

/**
 * Created by Michal on 28-Apr-16.
 */
public interface VizierResolver {
    List<VizierResult> findByQuery(VizierQuery query);
    boolean isAvailable();
}
