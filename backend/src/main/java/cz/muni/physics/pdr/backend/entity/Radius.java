package cz.muni.physics.pdr.backend.entity;

/**
 * Data class to store radius information
 *
 * @author Michal
 * @version 1.0
 * @since 28-Apr-16
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
        DEG("deg", 1),
        ARC_SEC("arcsec", 0.000277778),
        ARC_MIN("arcmin", 0.0166667);

        private String string;
        private double weight;

        Unit(String string, double weight) {
            this.string = string;
            this.weight = weight;
        }

        public double convertTo(double amount, Unit unit) {
            double inDegrees = amount * this.weight;
            return inDegrees / unit.weight;
        }

        @Override
        public String toString() {
            return string;
        }
    }
}
