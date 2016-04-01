package cz.muni.physics.java;

/**
 * @author Michal Krajčovič
 * @version 1.0
 * @since 25/03/16
 */
public class PhotometricData {
    private Double julianDate;
    private Double magnitude;
    private Double error;

    public PhotometricData() {
    }

    public PhotometricData(String julianDate, String magnitude, String error) {
        this.julianDate = Double.parseDouble(julianDate);
        this.magnitude = Double.parseDouble(magnitude);
        this.error = Double.parseDouble(error);
    }

    public PhotometricData(Double julianDate, Double magnitude, Double error) {
        this.julianDate = julianDate;
        this.magnitude = magnitude;
        this.error = error;
    }

    public Double getJulianDate() {
        return julianDate;
    }

    public void setJulianDate(Double julianDate) {
        this.julianDate = julianDate;
    }

    public Double getMagnitude() {
        return magnitude;
    }

    public void setMagnitude(Double magnitude) {
        this.magnitude = magnitude;
    }

    public Double getError() {
        return error;
    }

    public void setError(Double error) {
        this.error = error;
    }

    @Override
    public String toString() {
        return "PhotometricData{" +
                "julianDate='" + julianDate + '\'' +
                ", magnitude='" + magnitude + '\'' +
                ", error='" + error + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PhotometricData that = (PhotometricData) o;

        if (julianDate != null ? !julianDate.equals(that.julianDate) : that.julianDate != null) return false;
        if (magnitude != null ? !magnitude.equals(that.magnitude) : that.magnitude != null) return false;
        return error != null ? error.equals(that.error) : that.error == null;

    }

    @Override
    public int hashCode() {
        int result = julianDate != null ? julianDate.hashCode() : 0;
        result = 31 * result + (magnitude != null ? magnitude.hashCode() : 0);
        result = 31 * result + (error != null ? error.hashCode() : 0);
        return result;
    }
}
