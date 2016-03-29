package cz.muni.physics.java;

/**
 * @author Michal Krajčovič
 * @version 1.0
 * @since 25/03/16
 */
public class PhotometricData {
    private String julianDate;
    private String magnitude;
    private String error;

    public PhotometricData() {
    }

    public PhotometricData(String julianDate, String magnitude, String error) {
        this.julianDate = julianDate;
        this.magnitude = magnitude;
        this.error = error;
    }

    public String getJulianDate() {
        return julianDate;
    }

    public void setJulianDate(String julianDate) {
        this.julianDate = julianDate;
    }

    public String getMagnitude() {
        return magnitude;
    }

    public void setMagnitude(String magnitude) {
        this.magnitude = magnitude;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
