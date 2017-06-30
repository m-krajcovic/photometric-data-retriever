package cz.muni.physics.pdr.backend.entity;

/**
 * Data class for storing result from Vizier Service
 * @author Michal
 * @version 1.0
 * @since 28-Apr-16
 */
public class VizierResult {

    private String name;
    private double distance;
    private double epoch;
    private double period;
    private double rightAscension;
    private double declination;


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

    public double getRightAscension() {
        return rightAscension;
    }

    public void setRightAscension(double rightAscension) {
        this.rightAscension = rightAscension;
    }

    public double getDeclination() {
        return declination;
    }

    public void setDeclination(double declination) {
        this.declination = declination;
    }

    @Override
    public String toString() {
        return "VizierResult{" +
                "name='" + name + '\'' +
                ", distance=" + distance +
                ", epoch=" + epoch +
                ", period=" + period +
                ", rightAscension=" + rightAscension +
                ", declination=" + declination +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        VizierResult that = (VizierResult) o;

        if (Double.compare(that.epoch, epoch) != 0) return false;
        if (Double.compare(that.period, period) != 0) return false;
        if (Double.compare(that.rightAscension, rightAscension) != 0) return false;
        if (Double.compare(that.declination, declination) != 0) return false;
        return name != null ? name.equals(that.name) : that.name == null;

    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = name != null ? name.hashCode() : 0;
        temp = Double.doubleToLongBits(epoch);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(period);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(rightAscension);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(declination);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }
}
