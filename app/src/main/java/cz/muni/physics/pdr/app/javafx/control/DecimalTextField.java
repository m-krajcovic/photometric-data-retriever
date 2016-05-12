package cz.muni.physics.pdr.app.javafx.control;

import cz.muni.physics.pdr.app.javafx.formatter.DecimalFilter;
import javafx.beans.NamedArg;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.util.Locale;


/**
 * Created by Michal on 29-Apr-16.
 */
public class DecimalTextField extends TextField {

    private DoubleProperty value = new SimpleDoubleProperty(0);
    private Locale locale = Locale.ENGLISH;
    private String formatPattern = "#.0";
    private DecimalFormat format;

    public DecimalTextField() {
        this(Locale.getDefault());
    }

    public DecimalTextField(@NamedArg("locale") Locale locale) {
        this("#.0", locale);
    }

    public DecimalTextField(@NamedArg("formatPattern") String formatPattern) {
        this(formatPattern, Locale.getDefault());
    }

    public DecimalTextField(@NamedArg("formatPattern") String formatPattern, @NamedArg("locale") Locale locale) {
        format = new DecimalFormat(formatPattern, new DecimalFormatSymbols(locale));
        DecimalFilter filter = new DecimalFilter(format);
        super.setTextFormatter(new TextFormatter<>(filter));
        new DecimalFilter().getFormat().getDecimalFormatSymbols().getDecimalSeparator();
        textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.endsWith(String.valueOf(filter.getFormat().getDecimalFormatSymbols().getDecimalSeparator()))) {
                newValue = newValue.substring(0, newValue.length() - 1);
            }
            if (newValue.isEmpty()) {
                newValue = "0";
            }
            try {
                value.setValue(format.parse(newValue));
            } catch (ParseException e) {
                value.setValue(0);
            }
        });
    }


    public double getValue() {
        return value.get();
    }

    public DoubleProperty valueProperty() {
        return value;
    }

    public void setValue(double value) {
        setText(format.format(value));
    }
}
