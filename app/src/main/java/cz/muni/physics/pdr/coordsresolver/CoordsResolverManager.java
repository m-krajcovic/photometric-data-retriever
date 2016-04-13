package cz.muni.physics.pdr.coordsresolver;

import java.util.Map;

/**
 * @author Michal Krajčovič
 * @version 1.0
 * @since 13/04/16
 */
public interface CoordsResolverManager {
    CoordsResolverResult resolveFor(String ra, String dec, String rad);

    Map<CoordsResolver, Boolean> getAvailableCoordsResolvers();

}
