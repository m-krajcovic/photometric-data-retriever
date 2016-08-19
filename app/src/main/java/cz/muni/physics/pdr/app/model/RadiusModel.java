package cz.muni.physics.pdr.app.model;

import cz.muni.physics.pdr.backend.entity.Radius;
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
        DEG("deg", Radius.Unit.DEG),
        ARCSEC("arcsec", Radius.Unit.ARC_SEC),
        ARCMIN("arcmin", Radius.Unit.ARC_MIN);

        private String id;
        private Radius.Unit backendEnum;

        Unit(String id, Radius.Unit backendEnum) {
            this.id = id;
            this.backendEnum = backendEnum;
        }

        public Radius.Unit toBackend() {
            return backendEnum;
        }

        public Unit fromBackend(Radius.Unit backendEnum) {
            switch (backendEnum) {
                case ARC_MIN:
                    return RadiusModel.Unit.ARCMIN;
                case ARC_SEC:
                    return RadiusModel.Unit.ARCSEC;
                default:
                    return RadiusModel.Unit.DEG;
            }
        }

        @Override
        public String toString() {
            return id;
        }
    }
}
