package cz.muni.physics.pdr.app.controller;

import cz.muni.physics.pdr.app.model.PatternModel;
import cz.muni.physics.pdr.app.spring.AppConfig;
import cz.muni.physics.pdr.app.utils.FXMLUtils;
import cz.muni.physics.pdr.backend.exception.ResourceAvailabilityException;
import cz.muni.physics.pdr.backend.manager.StarSurveyManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ResourceBundle;
import java.util.regex.Pattern;

/**
 * @author Michal Krajčovič
 * @version 1.0
 * @since 23/04/16
 */
@Component
public class PatternsOverviewController {

    @Autowired
    private AppConfig app;
    @Autowired
    private StarSurveyManager starSurveyManager;

    @FXML
    private ResourceBundle resources;
    @FXML
    private TableView<PatternModel> tableView;
    @FXML
    private TableColumn<PatternModel, String> keyTableColumn;
    @FXML
    private TableColumn<PatternModel, Pattern> valueTableColumn;

    private Stage dialogStage;

    @FXML
    private void initialize() {
        keyTableColumn.setCellValueFactory(c -> c.getValue().getKey());
        valueTableColumn.setCellValueFactory(c -> c.getValue().getValue());

        ObservableList<PatternModel> list = FXCollections.observableArrayList();
        try {
            starSurveyManager.getAllPatterns().forEach((k, v) -> list.add(new PatternModel(k,v)));
        } catch (ResourceAvailabilityException e) {
            errorAlert();
        }
        tableView.setItems(list);
    }

    @FXML
    private void handleNewButton() {
        PatternModel patternModel = new PatternModel();
        boolean okClicked = app.showPatternEditDialog(patternModel, dialogStage);
        if (okClicked) {
            try {
                tableView.getItems().add(patternModel);
                starSurveyManager.insertPattern(patternModel.key(), patternModel.getValue().get());
            } catch (ResourceAvailabilityException e) {
                errorAlert();
            }
            tableView.refresh();
        }
    }


    @FXML
    private void handleEditButton() {
        PatternModel selectedRecord = tableView.getSelectionModel().getSelectedItem();
        if (selectedRecord != null) {
            boolean okClicked = app.showPatternEditDialog(selectedRecord, dialogStage);
            if (okClicked) {
                try {
                    starSurveyManager.insertPattern(selectedRecord.key(), selectedRecord.getValue().get());
                } catch (ResourceAvailabilityException e) {
                    errorAlert();
                }
                tableView.refresh();
            }
        } else {
            showNoSelectionDialog();
        }
    }

    @FXML
    private void handleDeleteButton() {
        PatternModel selectedRecord = tableView.getSelectionModel().getSelectedItem();
        if (selectedRecord != null) {
            try {
                tableView.getItems().remove(selectedRecord);
                starSurveyManager.removePattern(selectedRecord.key());
            } catch (ResourceAvailabilityException e) {
                errorAlert();
            }
            tableView.refresh();
        } else {
            showNoSelectionDialog();
        }
    }

    private void errorAlert() {
        FXMLUtils.alert("Resource not available", "Configuration file is broken.", "Try deleting config file and reloading application.", Alert.AlertType.ERROR).showAndWait();
    }


    private void showNoSelectionDialog() {
        Alert alert = FXMLUtils.alert(resources.getString("alert.noselection"),
                "No Pattern selected",
                "Please select a row in the table.",
                Alert.AlertType.WARNING);
        alert.initOwner(dialogStage);
        alert.showAndWait();
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }
}
