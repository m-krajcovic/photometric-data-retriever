package cz.muni.physics.controller;

import cz.muni.physics.MainApp;
import javafx.fxml.FXML;

/**
 * @author Michal Krajčovič
 * @version 1.0
 * @since 31/03/16
 */
public class RootLayoutController {
    private MainApp mainApp;

    @FXML
    private void handleDatabasesMenuItem(){
        mainApp.showDatabaseOverview();
    }

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }
}
