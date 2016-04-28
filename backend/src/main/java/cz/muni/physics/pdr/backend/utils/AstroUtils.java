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

    public static double distance(double ra1, double de1, double ra2, double de2) {
        return Math.hypot(ra1 - ra2, de1 - de2);
    }

    public static boolean areDegrees(String str) {
                /*
                 20 54 05.689 +37 01 17.38
                 10:12:45.3-45:17:50
                 15h17m-11d10m
                 15h17+89d15
                 275d11m15.6954s+17d59m59.876s
                 12.34567h-17.87654d
                 350.123456d-17.33333d <=> 350.123456 -17.33333
                 */
        return true;
    }
}
