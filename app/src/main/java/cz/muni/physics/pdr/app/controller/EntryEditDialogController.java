package cz.muni.physics.pdr.app.controller;

import cz.muni.physics.pdr.app.model.EntryModel;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * @author Michal Krajčovič
 * @version 1.0
 * @since 23/04/16
 */
@Component
@Scope("prototype")
public class EntryEditDialogController {

    @FXML
    private TextField keyTextField;
    @FXML
    private TextField valueTextField;

    private EntryModel model;
    private Stage dialogStage;
    private boolean okClicked;

    @FXML
    private void initialize() {
    }

    @FXML
    private void handleOkButton() {
        if (isInputValid()) {
            model.key(keyTextField.getText());
            model.value(valueTextField.getText());

            okClicked = true;
            dialogStage.close();
        }
    }

    private boolean isInputValid() {
        return !keyTextField.getText().isEmpty() && !valueTextField.getText().isEmpty();
    }

    @FXML
    private void handleCancelButton() {
        dialogStage.close();
    }

    public void setModel(EntryModel model) {
        this.model = model;
        keyTextField.setText(model.key());
        valueTextField.setText(model.value());
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public boolean isOkClicked() {
        return okClicked;
    }
}
