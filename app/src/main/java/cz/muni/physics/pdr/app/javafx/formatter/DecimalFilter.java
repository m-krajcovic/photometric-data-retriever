package cz.muni.physics.pdr.app.javafx.formatter;

import javafx.scene.control.TextFormatter;

import java.text.DecimalFormat;
import java.text.ParsePosition;
import java.util.function.UnaryOperator;

/**
 * Filter that allows only decimal numbers
 * @author Michal Krajčovič
 * @version 1.0
 * @since 17/04/16
 */
public class DecimalFilter implements UnaryOperator<TextFormatter.Change> {

    private DecimalFormat format;

    public DecimalFilter() {
        this(new DecimalFormat("#.0"));
    }

    public DecimalFilter(DecimalFormat format) {
        this.format = format;
    }

    @Override
    public TextFormatter.Change apply(TextFormatter.Change change) {
        if (change.getControlNewText().isEmpty()) {
            return change;
        }

        ParsePosition parsePosition = new ParsePosition(0);
        Object object = format.parse(change.getControlNewText(), parsePosition);
        if (object == null || parsePosition.getIndex() < change.getControlNewText().length()) {
            return null;
        } else {
            return change;
        }
    }

    public DecimalFormat getFormat() {
        return format;
    }
}
