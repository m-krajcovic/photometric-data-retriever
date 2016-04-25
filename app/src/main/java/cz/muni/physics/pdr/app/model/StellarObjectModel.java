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
public class StellarObjectModel implements EntityModel<StellarObject> {
    private StringProperty name;
    private DoubleProperty rightAscension;
    private DoubleProperty declination;
    private DoubleProperty distance;
    private DoubleProperty epoch;
    private DoubleProperty period;

    private StellarObject object;

    public StellarObjectModel(String name, double rightAscension, double declination, double distance, double epoch, double period) {
        this.name = new SimpleStringProperty(name);
        this.rightAscension = new SimpleDoubleProperty(rightAscension);
        this.declination = new SimpleDoubleProperty(declination);
        this.distance = new SimpleDoubleProperty(distance);
        this.epoch = new SimpleDoubleProperty(epoch);
        this.period = new SimpleDoubleProperty(period);
    }

    public StellarObjectModel(StellarObject stellarObject) {
        this(String.join(", ", stellarObject.getNames()),
                stellarObject.getRightAscension() != null ? stellarObject.getRightAscension() : 0,
                stellarObject.getDeclination() != null ? stellarObject.getDeclination() : 0,
                stellarObject.getDistance() != null ? stellarObject.getDistance() : 0,
                stellarObject.getEpoch() != null ? stellarObject.getEpoch() : 0,
                stellarObject.getPeriod() != null ? stellarObject.getPeriod() : 0);
        this.object = stellarObject;
    }

    @Override
    public StellarObject toEntity() {
        if (object != null) return object;
        StellarObject obj = new StellarObject();
        obj.getNames().add(getName());
        obj.setRightAscension(getRightAscension());
        obj.setDeclination(getDeclination());
        obj.setDistance(getDistance());
        return obj;
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

    public double getRightAscension() {
        return rightAscension.get();
    }

    public void setRightAscension(double rightAscension) {
        this.rightAscension.set(rightAscension);
    }

    public DoubleProperty rightAscensionProperty() {
        return rightAscension;
    }

    public double getDeclination() {
        return declination.get();
    }

    public void setDeclination(double declination) {
        this.declination.set(declination);
    }

    public DoubleProperty declinationProperty() {
        return declination;
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
