package cz.muni.physics.pdr.backend.utils;

import cz.muni.physics.pdr.backend.entity.CelestialCoordinates;

/**
 * @author Michal Krajčovič
 * @version 1.0
 * @since 16/04/16
 */
public class AstroUtils {
    public static boolean isInsideArea(double rightAscension, double declination, CelestialCoordinates area) {
        return area.getRadius() >= distance(rightAscension, declination, area.getRightAscension(), area.getDeclination());
    }

    public static double distance(double ra1, double de1, double ra2, double de2){
        return Math.hypot(ra1 - ra2, de1 - de2);
    }
}
