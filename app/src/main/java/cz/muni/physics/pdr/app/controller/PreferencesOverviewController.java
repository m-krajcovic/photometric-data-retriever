package cz.muni.physics.pdr.app.controller;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ResourceBundle;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

/**
 * @author Michal Krajčovič
 * @version 1.0
 * @since 22/04/16
 */
@Component
@Scope("prototype")
public class PreferencesOverviewController {

    @Value("${app.data.dir.path}")
    private String appDataDirPath;
    @Value("${plugins.dir.path}")
    private String pluginsDirPath;

    @Autowired
    private Preferences preferences;

    @FXML
    private ResourceBundle resources;
    @FXML
    private TextField pluginsRootTextField;
    @FXML
    private TextField appDataRootTextField;
    @FXML
    private Button applyButton;

    private BooleanProperty changeMade = new SimpleBooleanProperty(false);
    private StringProperty appDataRoot = new SimpleStringProperty();
    private StringProperty pluginsRoot = new SimpleStringProperty();

    private Stage dialogStage;

    @FXML
    private void initialize() {
        pluginsRootTextField.textProperty().bind(pluginsRoot);
        appDataRootTextField.textProperty().bind(appDataRoot);

        appDataRoot.setValue(appDataDirPath);
        pluginsRoot.setValue(pluginsDirPath);

        applyButton.disableProperty().bind(changeMade.not());
    }

    @FXML
    private void handleAppDataRootButton() {
        File chosen = showDirChooser(resources.getString("dir.chooser.app.data.title"), appDataRootTextField.getText());
        if (chosen != null) {
            String oldPath = appDataRoot.getValue();
            String newPath = chosen.getAbsolutePath();
            if (pluginsRoot.getValue().startsWith(oldPath)) {
                pluginsRoot.setValue(pluginsRoot.getValue().replaceAll(oldPath, newPath));
            }
            appDataRoot.set(newPath);
            changeMade.setValue(true);
        }
    }

    @FXML
    private void handlePluginsRootButton() {
        File chosen = showDirChooser(resources.getString("dir.chooser.plugins.title"), pluginsRootTextField.getText());
        if (chosen != null) {
            pluginsRoot.setValue(chosen.getAbsolutePath());
            changeMade.setValue(true);
        }
    }

    @FXML
    private void handleOkButton() {
        if (changeMade.getValue()) {
            handleApplyButton();
        }
        dialogStage.close();
    }

    @FXML
    private void handleApplyButton() {
        preferences.put("app.data.dir.path", appDataRoot.getValue());
        preferences.put("plugins.dir.path", pluginsRoot.getValue());
        changeMade.setValue(false);
    }

    private void resetToDefaults() {
        try {
            preferences.clear();
        } catch (BackingStoreException e) {
            e.printStackTrace();
        }
    }

    private File showDirChooser(String title, String initPath) {
        DirectoryChooser fileChooser = new DirectoryChooser();
        fileChooser.setTitle(title);
        fileChooser.setInitialDirectory(new File(initPath));
        return fileChooser.showDialog(dialogStage);
    }

    @FXML
    private void handleCancelButton(ActionEvent actionEvent) {
        changeMade.setValue(false);
        dialogStage.close();
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

}
