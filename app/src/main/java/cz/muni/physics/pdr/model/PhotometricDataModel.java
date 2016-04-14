package cz.muni.physics.pdr.model;

import cz.muni.physics.pdr.entity.PhotometricData;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

/**
 * @author Michal Krajčovič
 * @version 1.0
 * @since 25/03/16
 */
public class PhotometricDataModel implements EntityModel<PhotometricData> {
    private DoubleProperty julianDate = new SimpleDoubleProperty();
    private DoubleProperty magnitude = new SimpleDoubleProperty();
    private DoubleProperty error = new SimpleDoubleProperty();

    public PhotometricDataModel() {
    }

    public PhotometricDataModel(PhotometricData data) {
        this(data.getJulianDate(), data.getMagnitude(), data.getError());
    }

    public PhotometricDataModel(String julianDate, String magnitude, String error) {
        this(Double.parseDouble(julianDate), Double.parseDouble(magnitude), Double.parseDouble(error));
    }

    public PhotometricDataModel(Double julianDate, Double magnitude, Double error) {
        this.julianDate.setValue(julianDate);
        this.magnitude.setValue(magnitude);
        this.error.setValue(error);
    }

    public double getJulianDate() {
        return julianDate.get();
    }

    public DoubleProperty julianDateProperty() {
        return julianDate;
    }

    public void setJulianDate(double julianDate) {
        this.julianDate.set(julianDate);
    }

    public double getMagnitude() {
        return magnitude.get();
    }

    public DoubleProperty magnitudeProperty() {
        return magnitude;
    }

    public void setMagnitude(double magnitude) {
        this.magnitude.set(magnitude);
    }

    public double getError() {
        return error.get();
    }

    public DoubleProperty errorProperty() {
        return error;
    }

    public void setError(double error) {
        this.error.set(error);
    }

    @Override
    public String toString() {
        return "PhotometricDataModel{" +
                "julianDate='" + julianDate + '\'' +
                ", magnitude='" + magnitude + '\'' +
                ", error='" + error + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PhotometricDataModel that = (PhotometricDataModel) o;

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

    @Override
    public PhotometricData toEntity() {
        return new PhotometricData(getJulianDate(), getMagnitude(), getError());
    }
}
