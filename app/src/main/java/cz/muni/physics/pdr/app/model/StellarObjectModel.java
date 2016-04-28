package cz.muni.physics.pdr.app.model;

import cz.muni.physics.pdr.backend.entity.StellarObject;
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
    private StringProperty rightAscension;
    private StringProperty declination;
    private DoubleProperty distance;
    private DoubleProperty epoch;
    private DoubleProperty period;

    private StellarObject object;

    public StellarObjectModel(String name, String rightAscension, String declination, double distance, double epoch, double period) {
        this.name = new SimpleStringProperty(name);
        this.rightAscension = new SimpleStringProperty(rightAscension);
        this.declination = new SimpleStringProperty(declination);
        this.distance = new SimpleDoubleProperty(distance);
        this.epoch = new SimpleDoubleProperty(epoch);
        this.period = new SimpleDoubleProperty(period);
    }

    public String getRightAscension() {
        return rightAscension.get();
    }

    public StringProperty rightAscensionProperty() {
        return rightAscension;
    }

    public void setRightAscension(String rightAscension) {
        this.rightAscension.set(rightAscension);
    }

    public String getDeclination() {
        return declination.get();
    }

    public StringProperty declinationProperty() {
        return declination;
    }

    public void setDeclination(String declination) {
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
