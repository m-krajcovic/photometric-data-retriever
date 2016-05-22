package cz.muni.physics.pdr.app.model;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * @author Michal Krajčovič
 * @version 1.0
 * @since 16/04/16
 */
public class StellarObjectModel {
    private StringProperty name;
    private DoubleProperty rightAscension;
    private DoubleProperty declination;
    private DoubleProperty distance;
    private DoubleProperty epoch;
    private DoubleProperty period;

    public StellarObjectModel() {
        this.name = new SimpleStringProperty();
        this.rightAscension = new SimpleDoubleProperty();
        this.declination = new SimpleDoubleProperty();
        this.distance = new SimpleDoubleProperty();
        this.epoch = new SimpleDoubleProperty();
        this.period = new SimpleDoubleProperty();
    }

    public StellarObjectModel(String name, double rightAscension, double declination, double distance, double epoch, double period) {
        this.name = new SimpleStringProperty(name);
        this.rightAscension = new SimpleDoubleProperty(rightAscension);
        this.declination = new SimpleDoubleProperty(declination);
        this.distance = new SimpleDoubleProperty(distance);
        this.epoch = new SimpleDoubleProperty(epoch);
        this.period = new SimpleDoubleProperty(period);
    }

    public StellarObjectModel(String name, Double rightAscension, Double declination, Double distance, Double epoch, Double period) {
        this(name,
                rightAscension == null ? 0 : rightAscension,
                declination == null ? 0 : declination,
                distance == null ? 0 : distance,
                epoch == null ? 0 : epoch,
                period == null ? 0 : period);
    }

    public double getRightAscension() {
        return rightAscension.get();
    }

    public DoubleProperty rightAscensionProperty() {
        return rightAscension;
    }

    public void setRightAscension(double rightAscension) {
        this.rightAscension.set(rightAscension);
    }

    public double getDeclination() {
        return declination.get();
    }

    public DoubleProperty declinationProperty() {
        return declination;
    }

    public void setDeclination(double declination) {
        this.declination.set(declination);
    }

    public String getName() {
        return name.get();
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public StringProperty nameProperty() {
        return name;
    }

    public double getDistance() {
        return distance.get();
    }

    public void setDistance(double distance) {
        this.distance.set(distance);
    }

    public DoubleProperty distanceProperty() {
        return distance;
    }

    public double getEpoch() {
        return epoch.get();
    }

    public void setEpoch(double epoch) {
        this.epoch.set(epoch);
    }

    public DoubleProperty epochProperty() {
        return epoch;
    }

    public double getPeriod() {
        return period.get();
    }

    public void setPeriod(double period) {
        this.period.set(period);
    }

    public DoubleProperty periodProperty() {
        return period;
    }
}
