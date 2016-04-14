package cz.muni.physics.pdr.entity;

/**
 * @author Michal Krajčovič
 * @version 1.0
 * @since 14/04/16
 */
public class PhotometricData {
    private double julianDate;
    private double magnitude;
    private double error;

    public PhotometricData(double julianDate, double magnitude, double error) {
        this.julianDate = julianDate;
        this.magnitude = magnitude;
        this.error = error;
    }

    public PhotometricData(String julianDate, String magnitude, String error) {
        this.julianDate = Double.parseDouble(julianDate);
        this.magnitude = Double.parseDouble(magnitude);
        this.error = Double.parseDouble(error);
    }

    public double getJulianDate() {
        return julianDate;
    }

    public void setJulianDate(double julianDate) {
        this.julianDate = julianDate;
    }

    public double getMagnitude() {
        return magnitude;
    }

    public void setMagnitude(double magnitude) {
        this.magnitude = magnitude;
    }

    public double getError() {
        return error;
    }

    public void setError(double error) {
        this.error = error;
    }
}
