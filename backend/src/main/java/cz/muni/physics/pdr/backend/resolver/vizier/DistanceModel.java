package cz.muni.physics.pdr.backend.resolver.vizier;

public class DistanceModel<T> {
    private double distance;
    private T model;

    public DistanceModel(double distance, T model) {
        this.distance = distance;
        this.model = model;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public T getModel() {
        return model;
    }

    public void setModel(T model) {
        this.model = model;
    }
}
