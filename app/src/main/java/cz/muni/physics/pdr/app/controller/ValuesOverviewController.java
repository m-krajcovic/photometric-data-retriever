package cz.muni.physics.pdr.app.controller;

import cz.muni.physics.pdr.app.model.ValueParameterModel;
import cz.muni.physics.pdr.app.spring.Screens;
import cz.muni.physics.pdr.app.utils.FXMLUtils;
import cz.muni.physics.pdr.backend.exception.ResourceAvailabilityException;
import cz.muni.physics.pdr.backend.manager.StarSurveyManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ResourceBundle;

/**
 * @author Michal Krajčovič
 * @version 1.0
 * @since 23/04/16
 */
@Component
public class ValuesOverviewController extends StageController {

    @Autowired
    private Screens app;
    @Autowired
    private StarSurveyManager starSurveyManager;

    @FXML
    private ResourceBundle resources;
    @FXML
    private TableView<ValueParameterModel> tableView;
    @FXML
    private TableColumn<ValueParameterModel, String> keyTableColumn;
    @FXML
    private TableColumn<ValueParameterModel, String> valueTableColumn;

    @FXML
    private void initialize() {
        keyTableColumn.setCellValueFactory(c -> c.getValue().getKey());
        valueTableColumn.setCellValueFactory(c -> c.getValue().getValue());

        ObservableList<ValueParameterModel> list = FXCollections.observableArrayList();
        try {
            starSurveyManager.getAllValueParameters().forEach((k, v) -> list.add(new ValueParameterModel(k, v)));
        } catch (ResourceAvailabilityException e) {
            errorAlert();
        }
        tableView.setItems(list);
    }

    @FXML
    private void handleNewButton() {
        ValueParameterModel model = new ValueParameterModel();
        boolean okClicked = app.showEntryEditDialog(model, stage);
        if (okClicked) {
            try {
                tableView.getItems().add(model);
                starSurveyManager.insertValueParameter(model.key(), model.value());
            } catch (ResourceAvailabilityException e) {
                errorAlert();
            }
            tableView.refresh();
        }
    }


    @FXML
    private void handleEditButton() {
        ValueParameterModel model = tableView.getSelectionModel().getSelectedItem();
        if (model != null) {
            boolean okClicked = app.showEntryEditDialog(model, stage);
            if (okClicked) {
                try {
                    starSurveyManager.insertValueParameter(model.key(), model.value());
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
        ValueParameterModel selectedRecord = tableView.getSelectionModel().getSelectedItem();
        if (selectedRecord != null) {
            try {
                tableView.getItems().remove(selectedRecord);
                starSurveyManager.removeValueParameter(selectedRecord.key());
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
                "No value selected",
                "Please select a row in the table.",
                Alert.AlertType.WARNING);
        alert.initOwner(stage);
        alert.showAndWait();
    }
}
