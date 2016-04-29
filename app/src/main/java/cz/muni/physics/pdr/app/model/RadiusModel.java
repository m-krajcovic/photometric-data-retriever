package cz.muni.physics.pdr.app.model;

import javafx.beans.property.*;

/**
 * Created by Michal on 29-Apr-16.
 */
public class RadiusModel {
    private DoubleProperty radius = new SimpleDoubleProperty();
    private ObjectProperty<Unit> unit = new SimpleObjectProperty<>(Unit.DEG);

    public double getRadius() {
        return radius.get();
    }

    public DoubleProperty radiusProperty() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius.set(radius);
    }

    public Unit getUnit() {
        return unit.get();
    }

    public ObjectProperty<Unit> unitProperty() {
        return unit;
    }

    public void setUnit(Unit unit) {
        this.unit.set(unit);
    }

    public enum Unit {
        DEG("deg"),
        ARCSEC("arcsec"),
        ARCMIN("arcmin");

        private String id;

        private Unit(String id) {
            this.id = id;
        }


        @Override
        public String toString() {
            return id;
        }
    }
}
