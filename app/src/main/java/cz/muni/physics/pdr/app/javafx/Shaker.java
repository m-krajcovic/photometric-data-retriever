package cz.muni.physics.pdr.app.javafx;

import javafx.animation.TranslateTransition;
import javafx.scene.Node;
import javafx.util.Duration;

/**
 * Shaker class for shaking nodes in JavaFX
 * code taken from: http://stackoverflow.com/questions/36147786
 */
public class Shaker {
    private TranslateTransition tt;

    public Shaker(Node node) {
        tt = new TranslateTransition(Duration.millis(100), node);
        tt.setFromX(0f);
        tt.setByX(6f);
        tt.setCycleCount(4);
        tt.setAutoReverse(true);
    }

    public void shake() {
        tt.playFromStart();
    }
}
