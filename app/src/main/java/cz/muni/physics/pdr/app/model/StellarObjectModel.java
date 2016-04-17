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
public class StellarObjectModel implements EntityModel<StellarObject>{
    private StringProperty name;
    private DoubleProperty rightAscension;
    private DoubleProperty declination;
    private DoubleProperty distance;

    public StellarObjectModel(String name, double rightAscension, double declination, double distance) {
        this.name = new SimpleStringProperty(name);
        this.rightAscension = new SimpleDoubleProperty(rightAscension);
        this.declination = new SimpleDoubleProperty(declination);
        this.distance = new SimpleDoubleProperty(distance);
    }

    public StellarObjectModel(StellarObject result) {
        this(result.getNames().get(0), result.getRightAscension(), result.getDeclination(), result.getDistance());
    }

    @Override
    public StellarObject toEntity() {
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

    public StringProperty nameProperty() {
        return name;
    }

    public void setName(String name) {
        this.name.set(name);
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

    public double getDistance() {
        return distance.get();
    }

    public DoubleProperty distanceProperty() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance.set(distance);
    }

}
