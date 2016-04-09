package cz.muni.physics.controller;

import cz.muni.physics.model.Plugin;
import cz.muni.physics.model.StarSurvey;
import cz.muni.physics.utils.AppConfig;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Michal Krajčovič
 * @version 1.0
 * @since 01/04/16
 */
@Component
public class StarSurveyEditDialogController {

    @Autowired
    private AppConfig app;

    @FXML
    private TextField nameTextField;
    @FXML
    private ChoiceBox<Plugin> pluginChoiceBox;
    @FXML
    private TextArea sesameIdentifierTextArea;
    @FXML
    private TextArea urlTextArea;

    private Stage dialogStage;
    private StarSurvey starSurvey;
    private boolean okClicked = false;


    @FXML
    private void initialize(){
        pluginChoiceBox.setConverter(new StringConverter<Plugin>() {
            @Override
            public String toString(Plugin object) {
                return object.getName();
            }

            @Override
            public Plugin fromString(String string) {
                return null;
            }
        });
        pluginChoiceBox.setItems(app.getPlugins());
    }

    @FXML
    private void handleOk() {
        if (isInputValid()){
            starSurvey.setName(nameTextField.getText());
            starSurvey.setSesameAlias(sesameIdentifierTextArea.getText());
            starSurvey.setURL(urlTextArea.getText());
            starSurvey.setPlugin(pluginChoiceBox.getValue());

            okClicked = true;
            dialogStage.close();
        }
    }

    private boolean isInputValid() {
        return !nameTextField.getText().isEmpty() && StringUtils.isAlphanumericSpace(nameTextField.getText())
                && !urlTextArea.getText().isEmpty()
                && pluginChoiceBox.getValue() != null;
    }

    @FXML
    private void handleCancel(ActionEvent actionEvent) {
        dialogStage.close();
    }

    public void setStarSurvey(StarSurvey starSurvey) {
        this.starSurvey = starSurvey;
        nameTextField.setText(starSurvey.getName());
        sesameIdentifierTextArea.setText(starSurvey.getSesameAlias());
        urlTextArea.setText(starSurvey.getURL());
        pluginChoiceBox.getSelectionModel().select(starSurvey.getPlugin());
    }

    public boolean isOkClicked() {
        return okClicked;
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }
}
