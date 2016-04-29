package cz.muni.physics.pdr.app.javafx.control;

import cz.muni.physics.pdr.app.javafx.formatter.DecimalFilter;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;


/**
 * Created by Michal on 29-Apr-16.
 */
public class DecimalTextField extends TextField {

    private DoubleProperty value = new SimpleDoubleProperty(0.05);

    public DecimalTextField() {
        super.setTextFormatter(new TextFormatter<>(new DecimalFilter()));
        textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.endsWith(".")) {
                newValue = newValue.substring(0, newValue.length() - 1);
            }
            if (newValue.isEmpty()) {
                newValue = "0.05";
            }
            value.setValue(Double.parseDouble(newValue));
        });
    }

    public double getValue() {
        return value.get();
    }

    public DoubleProperty valueProperty() {
        return value;
    }

    public void setValue(double value) {
        setText(Double.toString(value));
    }
}
