package cz.muni.physics.pdr.app.controller;

import cz.muni.physics.pdr.app.model.PluginModel;
import cz.muni.physics.pdr.app.model.StarSurveyModel;
import cz.muni.physics.pdr.app.utils.FXMLUtils;
import cz.muni.physics.pdr.backend.manager.PluginManager;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ResourceBundle;

/**
 * @author Michal Krajčovič
 * @version 1.0
 * @since 01/04/16
 */
@Component
@Scope("prototype")
public class StarSurveyEditDialogController extends StageController {

    @Autowired
    private PluginManager pluginManager;

    @FXML
    private ResourceBundle resources;
    @FXML
    private TextField nameTextField;
    @FXML
    private ChoiceBox<PluginModel> pluginChoiceBox;
    @FXML
    private TextArea urlTextArea;

    private StarSurveyModel starSurvey;
    private boolean okClicked = false;


    @FXML
    private void initialize() {
        pluginChoiceBox.setConverter(new StringConverter<PluginModel>() {
            @Override
            public String toString(PluginModel object) {
                return object.getName();
            }

            @Override
            public PluginModel fromString(String string) {
                return null;
            }
        });
        ObservableList<PluginModel> list = FXCollections.observableArrayList();
        pluginManager.getAll().forEach(p -> list.add(new PluginModel(p)));
        pluginChoiceBox.setItems(list);
        Platform.runLater(() -> nameTextField.requestFocus());
    }

    @FXML
    private void handleOk() {
        if (isInputValid()) {
            starSurvey.setName(nameTextField.getText());
            starSurvey.setPlugin(pluginChoiceBox.getValue());

            okClicked = true;
            stage.close();
        } else {
            FXMLUtils.alert(resources.getString("input.error"), resources.getString("input.is.not.valid"), resources.getString("check.try.input"), Alert.AlertType.ERROR).showAndWait();
        }
    }

    private boolean isInputValid() {
        return !nameTextField.getText().isEmpty() && StringUtils.isAlphanumericSpace(nameTextField.getText())
                && pluginChoiceBox.getValue() != null;
    }

    @FXML
    private void handleCancel() {
        stage.close();
    }

    public void setStarSurvey(StarSurveyModel starSurvey) {
        this.starSurvey = starSurvey;
        nameTextField.setText(starSurvey.getName());
        urlTextArea.setText(String.join("\n", starSurvey.getUrls()));
        pluginChoiceBox.getSelectionModel().select(starSurvey.getPlugin());
    }

    public boolean isOkClicked() {
        return okClicked;
    }

}
