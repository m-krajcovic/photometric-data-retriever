package cz.muni.physics.pdr.app.controller;

import javafx.stage.Stage;

/**
 * @author Michal Krajčovič
 * @version 1.0
 * @since 25/04/16
 */
public abstract class StageController {
    protected Stage stage;

    public void setStage(Stage stage) {
        this.stage = stage;
    }
}
