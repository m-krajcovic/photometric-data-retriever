package cz.muni.physics.pdr.coordsresolver;

/**
 * @author Michal Krajčovič
 * @version 1.0
 * @since 13/04/16
 */
public interface CoordsResolver {
    CoordsResolverResult getResult(String ra, String dec, String rad);

    boolean isAvailable();
}
