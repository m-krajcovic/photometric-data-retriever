package cz.muni.physics.pdr.app.controller;

import cz.muni.physics.pdr.app.spring.ScreenConfig;
import javafx.fxml.FXML;
import javafx.scene.control.MenuBar;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Michal Krajčovič
 * @version 1.0
 * @since 31/03/16
 */
@Component
public class RootLayoutController {
    @Autowired
    private ScreenConfig app;
    @FXML
    private MenuBar menuBar;

    @FXML
    private void initialize() {
        final String os = System.getProperty("os.name");
        if (os != null && os.startsWith("Mac"))
            menuBar.useSystemMenuBarProperty().set(true);
    }

    @FXML
    private void handleDatabasesMenuItem() {
        app.showStarSurveyOverview();
    }

    @FXML
    private void handlePreferencesMenuItem() {
        app.showPreferencesOverview();
    }

    @FXML
    private void handleCloseMenuItem() {
        app.close();
    }
}
