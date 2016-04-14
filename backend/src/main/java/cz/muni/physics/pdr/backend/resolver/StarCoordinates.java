package cz.muni.physics.pdr.backend.resolver;

/**
 * @author Michal Krajčovič
 * @version 1.0
 * @since 13/04/16
 */
public class StarCoordinates {

    private double rightAscension;
    private double declination;
    private double radius;

    public StarCoordinates(double rightAscension, double declination, double radius) {
        this.rightAscension = rightAscension;
        this.declination = declination;
        this.radius = radius;
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

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StarCoordinates that = (StarCoordinates) o;

        if (Double.compare(that.getRightAscension(), getRightAscension()) != 0) return false;
        if (Double.compare(that.getDeclination(), getDeclination()) != 0) return false;
        return Double.compare(that.getRadius(), getRadius()) == 0;

    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = Double.doubleToLongBits(getRightAscension());
        result = (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(getDeclination());
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(getRadius());
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }
}
