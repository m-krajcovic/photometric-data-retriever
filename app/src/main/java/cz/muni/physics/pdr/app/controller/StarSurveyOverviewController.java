package cz.muni.physics.pdr.app.controller;

import cz.muni.physics.pdr.app.javafx.cell.PluginCellFactory;
import cz.muni.physics.pdr.app.model.PluginModel;
import cz.muni.physics.pdr.app.model.StarSurveyModel;
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
 * @since 31/03/16
 */
@Component
public class StarSurveyOverviewController extends StageController {

    @Autowired
    private Screens app;
    @Autowired
    private StarSurveyManager starSurveyManager;

    @FXML
    private ResourceBundle resources;
    @FXML
    private TableView<StarSurveyModel> starSurveys;
    @FXML
    private TableColumn<StarSurveyModel, String> nameColumn;
    @FXML
    private TableColumn<StarSurveyModel, PluginModel> pluginColumn;


    @FXML
    private void initialize() {
        nameColumn.setCellValueFactory(cell -> cell.getValue().nameProperty());
        pluginColumn.setCellValueFactory(cell -> cell.getValue().pluginProperty());

        pluginColumn.setCellFactory(new PluginCellFactory());

        ObservableList<StarSurveyModel> list = FXCollections.observableArrayList();
        try {
            starSurveyManager.getAll().forEach(s -> list.add(new StarSurveyModel(s)));
        } catch (ResourceAvailabilityException e) {
            errorAlert();
        }
        starSurveys.setItems(list);
    }

    @FXML
    private void handleNewButton() {
        StarSurveyModel tempStarSurvey = new StarSurveyModel();
        boolean okClicked = app.showStarSurveyEditDialog(tempStarSurvey, stage);
        if (okClicked) {
            try {
                starSurveys.getItems().add(tempStarSurvey);
                starSurveyManager.insert(tempStarSurvey.toEntity());
            } catch (ResourceAvailabilityException e) {
                errorAlert();
            }
            starSurveys.refresh();
        }
    }

    @FXML
    private void handleEditButton() {
        StarSurveyModel selectedRecord = starSurveys.getSelectionModel().getSelectedItem();
        if (selectedRecord != null) {
            boolean okClicked = app.showStarSurveyEditDialog(selectedRecord, stage);
            if (okClicked) {
                try {
                    starSurveyManager.insert(selectedRecord.toEntity());
                } catch (ResourceAvailabilityException e) {
                    errorAlert();
                }
                starSurveys.refresh();
            }
        } else {
            showNoSelectionDialog();
        }
    }

    @FXML
    private void handleDeleteButton() {
        StarSurveyModel selectedRecord = starSurveys.getSelectionModel().getSelectedItem();
        if (selectedRecord != null) {
            try {
                starSurveys.getItems().remove(selectedRecord);
                starSurveyManager.delete(selectedRecord.toEntity());
            } catch (ResourceAvailabilityException e) {
                errorAlert();
            }
            starSurveys.refresh();
        } else {
            showNoSelectionDialog();
        }
    }

    @FXML
    private void handleValuesButton() {
        app.showValueParameterOverview(stage);
    }

    @FXML
    private void handlePatternsButton() {
        app.showPatternsOverview(stage);
    }

    private void errorAlert() {
        FXMLUtils.alert(resources.getString("resource.not.available"), resources.getString("configuration.file.is.broken"), resources.getString("delete.reload.app"), Alert.AlertType.ERROR).showAndWait();
    }

    private void showNoSelectionDialog() {
        Alert alert = FXMLUtils.alert(resources.getString("alert.noselection"),
                resources.getString("no.star.survey.selected"),
                resources.getString("select.row.table"),
                Alert.AlertType.WARNING);
        alert.initOwner(stage);
        alert.showAndWait();
    }

}
