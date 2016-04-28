package cz.muni.physics.pdr.backend.entity;

/**
 * Created by Michal on 28-Apr-16.
 */
public class VizierResult {

    private String name;
    private double distance;
    private double epoch;
    private double period;
    private String rightAscension;
    private String Declination;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public double getEpoch() {
        return epoch;
    }

    public void setEpoch(double epoch) {
        this.epoch = epoch;
    }

    public double getPeriod() {
        return period;
    }

    public void setPeriod(double period) {
        this.period = period;
    }

    public String getRightAscension() {
        return rightAscension;
    }

    public void setRightAscension(String rightAscension) {
        this.rightAscension = rightAscension;
    }

    public String getDeclination() {
        return Declination;
    }

    public void setDeclination(String declination) {
        Declination = declination;
    }
}
