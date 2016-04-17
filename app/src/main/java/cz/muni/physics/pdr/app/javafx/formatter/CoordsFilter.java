package cz.muni.physics.pdr.app.javafx.formatter;

import javafx.scene.control.TextFormatter;

import java.text.DecimalFormat;
import java.text.ParsePosition;
import java.util.Arrays;
import java.util.function.UnaryOperator;

/**
 * @author Michal Krajčovič
 * @version 1.0
 * @since 17/04/16
 */
public class CoordsFilter implements UnaryOperator<TextFormatter.Change> {

    private DecimalFormat format = new DecimalFormat("#.0");

    @Override
    public TextFormatter.Change apply(TextFormatter.Change change) {
        if (change.getControlNewText().isEmpty()) {
            return change;
        }
        String text = change.getControlNewText();
        String[] split = text.split(" ");
        if (split.length == 2 && change.getText().equals(" ")) {
            return null;
        }
        if (Arrays.stream(split).allMatch(s -> {
            if (s.startsWith("+")) {
                s = s.substring(1);
                if (s.isEmpty()) return true;
            }
            ParsePosition parsePosition = new ParsePosition(0);
            Object object = format.parse(s, parsePosition);

            return (!(object == null || parsePosition.getIndex() < s.length()));
        })) {
            return change;
        }
        return null;
    }
}
