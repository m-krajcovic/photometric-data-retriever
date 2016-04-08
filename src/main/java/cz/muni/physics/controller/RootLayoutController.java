package cz.muni.physics.controller;

import cz.muni.physics.MainApp;
import javafx.fxml.FXML;
import javafx.scene.control.MenuBar;

/**
 * @author Michal Krajčovič
 * @version 1.0
 * @since 31/03/16
 */
public class RootLayoutController {
    public MenuBar menuBar;
    private MainApp mainApp;

    @FXML
    private void initialize(){
        final String os = System.getProperty ("os.name");
        if (os != null && os.startsWith ("Mac"))
            menuBar.useSystemMenuBarProperty ().set (true);
    }

    @FXML
    private void handleDatabasesMenuItem(){
        mainApp.showStarSurveyOverview();
    }

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }
}
