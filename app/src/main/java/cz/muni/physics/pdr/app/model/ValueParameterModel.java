package cz.muni.physics.pdr.app.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * @author Michal Krajčovič
 * @version 1.0
 * @since 23/04/16
 */
public class ValueParameterModel extends EntryModel<StringProperty, StringProperty> {
    public ValueParameterModel() {
    }

    public ValueParameterModel(StringProperty key, StringProperty value) {
        super(key, value);
    }

    public ValueParameterModel(String key, String value) {
        super(new SimpleStringProperty(key), new SimpleStringProperty(value));
    }

    @Override
    public void key(String keyString) {
        key = new SimpleStringProperty(keyString);
    }

    @Override
    public void value(String valueString) {
        value = new SimpleStringProperty(valueString);
    }

    public String key() {
        return key == null ? "" : key.getValue();
    }

    public String value() {
        return value == null ? "" : value.getValue();
    }
}
