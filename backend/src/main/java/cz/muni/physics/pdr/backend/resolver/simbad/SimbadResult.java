package cz.muni.physics.pdr.backend.resolver.simbad;

/**
 * @author Michal
 * @version 1.0
 * @since 8/10/2016
 */
public class SimbadResult {
    private double distance;
    private String identifier;
    private String rightAscension;
    private String declination;

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getRightAscension() {
        return rightAscension;
    }

    public void setRightAscension(String rightAscension) {
        this.rightAscension = rightAscension;
    }

    public String getDeclination() {
        return declination;
    }

    public void setDeclination(String declination) {
        this.declination = declination;
    }
}
