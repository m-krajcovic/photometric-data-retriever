package cz.muni.physics.pdr.app.model;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.regex.Pattern;

/**
 * @author Michal Krajčovič
 * @version 1.0
 * @since 23/04/16
 */
public class PatternModel extends EntryModel<StringProperty, ObjectProperty<Pattern>> {

    public PatternModel() {
    }

    public PatternModel(StringProperty key, ObjectProperty<Pattern> value) {
        super(key, value);
    }

    public PatternModel(String key, Pattern value) {
        super(new SimpleStringProperty(key), new SimpleObjectProperty<>(value));
    }

    @Override
    public void key(String keyString) {
        key = new SimpleStringProperty(keyString);
    }

    @Override
    public void value(String valueString) {
        value = new SimpleObjectProperty<>(Pattern.compile(valueString));
    }

    public String key() {
        return key == null ? "" : key.getValue();
    }

    public String value() {
        return value == null ? "" : value.getValue().pattern();
    }
}
