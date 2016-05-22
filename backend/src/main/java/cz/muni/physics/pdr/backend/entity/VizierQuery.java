package cz.muni.physics.pdr.backend.entity;

/**
 * Data class for storing query for Vizier service
 * @author Michal
 * @version 1.0
 * @since 28-Apr-16
 */
public class VizierQuery {
    private String query;
    private Radius radius;

    public VizierQuery(String query) {
        this.query = query;
    }

    public VizierQuery(String query, Radius radius) {
        this.query = query;
        this.radius = radius;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public Radius getRadius() {
        return radius;
    }

    public void setRadius(Radius radius) {
        this.radius = radius;
    }
}
