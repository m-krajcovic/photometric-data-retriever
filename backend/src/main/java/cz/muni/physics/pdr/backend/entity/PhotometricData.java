package cz.muni.physics.pdr.backend.entity;

import cz.muni.physics.pdr.backend.utils.NumberUtils;

/**
 * Data class to store measured photometric data of a variable star
 * @author Michal Krajčovič
 * @version 1.0
 * @since 14/04/16
 */
public class PhotometricData {
    private double julianDate;
    private double magnitude;
    private double error;
    private String id = null;

    public PhotometricData(double julianDate, double magnitude, double error) {
        this.julianDate = julianDate;
        this.magnitude = magnitude;
        this.error = error;
    }

    public PhotometricData(String julianDate, String magnitude, String error) {
        this(NumberUtils.isParsable(julianDate) ? Double.parseDouble(julianDate) : 0,
                NumberUtils.isParsable(magnitude) ? Double.parseDouble(magnitude) : 0,
                NumberUtils.isParsable(error) ? Double.parseDouble(error) : 0);
    }

    public PhotometricData(double julianDate, double magnitude, double error, String id) {
        this(julianDate, magnitude, error);
        this.id = id;
    }

    public PhotometricData(String julianDate, String magnitude, String error, String id) {
        this(julianDate, magnitude, error);
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
