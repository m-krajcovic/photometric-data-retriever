package cz.muni.physics.pdr.app.model;

import cz.muni.physics.pdr.backend.entity.PhotometricData;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * @author Michal Krajčovič
 * @version 1.0
 * @since 25/03/16
 */
public class PhotometricDataModel {
    private DoubleProperty julianDate = new SimpleDoubleProperty();
    private DoubleProperty magnitude = new SimpleDoubleProperty();
    private DoubleProperty error = new SimpleDoubleProperty();
    private StringProperty id = new SimpleStringProperty("");

    public PhotometricDataModel() {
    }

    public PhotometricDataModel(PhotometricData data) {
        this(data.getJulianDate(), data.getMagnitude(), data.getError());
        if (data.getId() != null) id.setValue(data.getId());
    }

    public PhotometricDataModel(String julianDate, String magnitude, String error) {
        this(Double.parseDouble(julianDate), Double.parseDouble(magnitude), Double.parseDouble(error));
    }

    public PhotometricDataModel(Double julianDate, Double magnitude, Double error) {
        this.julianDate.setValue(julianDate);
        this.magnitude.setValue(magnitude);
        this.error.setValue(error);
    }

    public String getId() {
        return id.get();
    }

    public StringProperty idProperty() {
        return id;
    }

    public void setId(String id) {
        this.id.set(id);
    }

    public double getJulianDate() {
        return julianDate.get();
    }

    public void setJulianDate(double julianDate) {
        this.julianDate.set(julianDate);
    }

    public DoubleProperty julianDateProperty() {
        return julianDate;
    }

    public double getMagnitude() {
        return magnitude.get();
    }

    public void setMagnitude(double magnitude) {
        this.magnitude.set(magnitude);
    }

    public DoubleProperty magnitudeProperty() {
        return magnitude;
    }

    public double getError() {
        return error.get();
    }

    public void setError(double error) {
        this.error.set(error);
    }

    public DoubleProperty errorProperty() {
        return error;
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

    public PhotometricData toEntity() {
        return new PhotometricData(getJulianDate(), getMagnitude(), getError());
    }
}
