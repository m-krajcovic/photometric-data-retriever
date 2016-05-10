package cz.muni.physics.pdr.app.controller;

import cz.muni.physics.pdr.app.model.PatternModel;
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
import javafx.scene.control.TextField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ResourceBundle;
import java.util.function.BiPredicate;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * @author Michal Krajčovič
 * @version 1.0
 * @since 23/04/16
 */
@Component
public class PatternsOverviewController extends StageController {

    @Autowired
    private Screens app;
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

    private BiPredicate<TextField, TextField> validCheck;

    @FXML
    private void initialize() {
        keyTableColumn.setCellValueFactory(c -> c.getValue().getKey());
        valueTableColumn.setCellValueFactory(c -> c.getValue().getValue());

        ObservableList<PatternModel> list = FXCollections.observableArrayList();
        try {
            starSurveyManager.getAllPatterns().forEach((k, v) -> list.add(new PatternModel(k, v)));
        } catch (ResourceAvailabilityException e) {
            errorAlert();
        }
        tableView.setItems(list);

        validCheck = (key, value) -> {
            boolean isRegex;
            try {
                Pattern.compile(value.getText());
                isRegex = true;
            } catch (PatternSyntaxException e) {
                isRegex = false;
            }
            return !key.getText().isEmpty()
                    && isRegex;
        };
    }

    @FXML
    private void handleNewButton() {
        PatternModel patternModel = new PatternModel();
        boolean okClicked = app.showEntryEditDialog(patternModel, stage, validCheck);
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
            boolean okClicked = app.showEntryEditDialog(selectedRecord, stage, validCheck);
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
        FXMLUtils.alert(resources.getString("resource.not.available"), resources.getString("configuration.file.is.broken"), resources.getString("delete.reload.app"), Alert.AlertType.ERROR).showAndWait();
    }

    private void showNoSelectionDialog() {
        Alert alert = FXMLUtils.alert(resources.getString("alert.noselection"),
                resources.getString("no.pattern.selected"),
                resources.getString("select.row.table"),
                Alert.AlertType.WARNING);
        alert.initOwner(stage);
        alert.showAndWait();
    }

}
