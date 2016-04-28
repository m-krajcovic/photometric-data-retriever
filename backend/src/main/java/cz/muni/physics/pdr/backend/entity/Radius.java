package cz.muni.physics.pdr.backend.entity;

/**
 * Created by Michal on 28-Apr-16.
 */
public class Radius {
    private double radius;
    private Unit unit;

    public Radius(double radius, Unit unit) {
        this.radius = radius;
        this.unit = unit;
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public Unit getUnit() {
        return unit;
    }

    public void setUnit(Unit unit) {
        this.unit = unit;
    }

    public enum Unit {
        DEG("deg"),
        ARC_SEC("arcsec"),
        ARC_MIN("arcmin");

        private String string;

        private Unit(String string) {
            this.string = string;
        }

        @Override
        public String toString() {
            return string;
        }
    }
}
