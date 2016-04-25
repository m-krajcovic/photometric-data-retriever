package cz.muni.physics.pdr.app.controller;

import cz.muni.physics.pdr.app.model.EntryModel;
import cz.muni.physics.pdr.app.utils.FXMLUtils;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.function.BiPredicate;

/**
 * @author Michal Krajčovič
 * @version 1.0
 * @since 23/04/16
 */
@Component
@Scope("prototype")
public class EntryEditDialogController extends StageController {

    @FXML
    private TextField keyTextField;
    @FXML
    private TextField valueTextField;

    private BiPredicate<TextField, TextField> validCheck;

    private EntryModel model;
    private boolean okClicked;

    @FXML
    private void initialize() {
        Platform.runLater(() -> keyTextField.requestFocus());
    }

    @FXML
    private void handleOkButton() {
        if (isInputValid()) {
            model.key(keyTextField.getText());
            model.value(valueTextField.getText());
            okClicked = true;
            stage.close();
        } else {
            FXMLUtils.alert("Input error", "Input is not valid", "Please check your input and try again", Alert.AlertType.ERROR).showAndWait();
        }
    }
    
    @FXML
    private void handleCancelButton() {
        stage.close();
    }

    private boolean isInputValid() {
        return validCheck != null ? validCheck.test(keyTextField, valueTextField) : !keyTextField.getText().isEmpty() && !valueTextField.getText().isEmpty();
    }

    public void setModel(EntryModel model) {
        this.model = model;
        keyTextField.setText(model.key());
        valueTextField.setText(model.value());
    }

    public void setValidCheck(BiPredicate<TextField, TextField> validCheck) {
        this.validCheck = validCheck;
    }

    public boolean isOkClicked() {
        return okClicked;
    }
}
