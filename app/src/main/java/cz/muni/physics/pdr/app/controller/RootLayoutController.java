package cz.muni.physics.pdr.app.controller;

import cz.muni.physics.pdr.app.spring.AppConfig;
import javafx.fxml.FXML;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
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
    private AppConfig app;
    @FXML
    private MenuBar menuBar;
    @FXML
    private MenuItem surveysMenuItem;
    @FXML
    private MenuItem preferencesMenuItem;
    @FXML
    private MenuItem closeMenuItem;

    @FXML
    private void initialize() {
        final String os = System.getProperty("os.name");
        if (os != null && os.startsWith("Mac"))
            menuBar.useSystemMenuBarProperty().set(true);
        closeMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.W, KeyCombination.SHORTCUT_DOWN));
        preferencesMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.P, KeyCombination.SHORTCUT_DOWN));
        surveysMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.SHORTCUT_DOWN));

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
        app.getPrimaryStage().close();
    }
}
