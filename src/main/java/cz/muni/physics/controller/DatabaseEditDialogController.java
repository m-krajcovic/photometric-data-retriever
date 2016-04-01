package cz.muni.physics.controller;

import cz.muni.physics.MainApp;
import cz.muni.physics.model.DatabaseRecord;
import cz.muni.physics.model.Plugin;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.StringConverter;

/**
 * @author Michal Krajčovič
 * @version 1.0
 * @since 01/04/16
 */
public class DatabaseEditDialogController {

    private MainApp mainApp;

    @FXML
    private TextField nameTextField;
    @FXML
    private ChoiceBox<Plugin> pluginChoiceBox;
    @FXML
    private TextArea sesameIdentifierTextArea;
    @FXML
    private TextArea urlTextArea;

    private Stage dialogStage;
    private DatabaseRecord dbRecord;
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
    }

    @FXML
    private void handleOk() {
        if (isInputValid()){
            dbRecord.setName(nameTextField.getText());
            dbRecord.setSesameAlias(sesameIdentifierTextArea.getText());
            dbRecord.setURL(urlTextArea.getText());
            dbRecord.setPlugin(pluginChoiceBox.getValue());

            okClicked = true;
            dialogStage.close();
        }
    }

    private boolean isInputValid() {
        // TODO verify this shit lol.
        return true;
    }

    @FXML
    private void handleCancel(ActionEvent actionEvent) {
        dialogStage.close();
    }

    public void setDbRecord(DatabaseRecord dbRecord) {
        this.dbRecord = dbRecord;
        nameTextField.setText(dbRecord.getName());
        sesameIdentifierTextArea.setText(dbRecord.getSesameAlias());
        urlTextArea.setText(dbRecord.getURL());
        pluginChoiceBox.getSelectionModel().select(dbRecord.getPlugin());
    }

    public boolean isOkClicked() {
        return okClicked;
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
        pluginChoiceBox.setItems(mainApp.getPlugins());
    }
}
