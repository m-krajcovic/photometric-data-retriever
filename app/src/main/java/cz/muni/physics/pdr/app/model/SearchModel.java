package cz.muni.physics.pdr.app.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Created by Michal on 29-Apr-16.
 */
public class SearchModel {
    private StringProperty query = new SimpleStringProperty("");
    private RadiusModel radius = new RadiusModel();

    public RadiusModel getRadius() {
        return radius;
    }

    public void setRadius(RadiusModel radius) {
        this.radius = radius;
    }

    public String getQuery() {
        return query.get();
    }

    public StringProperty queryProperty() {
        return query;
    }

    public void setQuery(String query) {
        this.query.set(query);
    }
}
