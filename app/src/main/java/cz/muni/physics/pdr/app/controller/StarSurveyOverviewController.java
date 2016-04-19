package cz.muni.physics.pdr.app.controller;

import cz.muni.physics.pdr.app.javafx.cell.PluginCellFactory;
import cz.muni.physics.pdr.app.model.PluginModel;
import cz.muni.physics.pdr.app.model.StarSurveyModel;
import cz.muni.physics.pdr.app.utils.ScreenConfig;
import cz.muni.physics.pdr.backend.exception.ResourceAvailabilityException;
import cz.muni.physics.pdr.backend.manager.StarSurveyManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Michal Krajčovič
 * @version 1.0
 * @since 31/03/16
 */
@Component
public class StarSurveyOverviewController {

    @Autowired
    private ScreenConfig app;
    @Autowired
    private StarSurveyManager starSurveyManager;

    @FXML
    private TableView<StarSurveyModel> starSurveys;
    @FXML
    private TableColumn<StarSurveyModel, String> nameColumn;
    @FXML
    private TableColumn<StarSurveyModel, String> sesameIdentifierColumn;
    @FXML
    private TableColumn<StarSurveyModel, String> urlColumn;
    @FXML
    private TableColumn<StarSurveyModel, PluginModel> pluginColumn;
    @FXML
    private Button button;

    public StarSurveyOverviewController() {

    }

    @FXML
    private void handleNewButton() {
        StarSurveyModel tempStarSurvey = new StarSurveyModel();
        boolean okClicked = app.showStarSurveyEditDialog(tempStarSurvey);
        if (okClicked) {
            try {
                starSurveyManager.insert(tempStarSurvey.toEntity());
            } catch (ResourceAvailabilityException e) {
                e.printStackTrace(); //todo
            }
        }
    }

    @FXML
    private void handleEditButton() {
        StarSurveyModel selectedRecord = starSurveys.getSelectionModel().getSelectedItem();
        if (selectedRecord != null) {
            boolean okClicked = app.showStarSurveyEditDialog(selectedRecord);
            if (okClicked) {
                try {
                    starSurveyManager.insert(selectedRecord.toEntity());
                } catch (ResourceAvailabilityException e) {
                    e.printStackTrace(); //todo
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
            try{
                starSurveyManager.delete(selectedRecord.toEntity());
            } catch (ResourceAvailabilityException e) {
                e.printStackTrace(); // todo
            }
        } else {
            showNoSelectionDialog();
        }
    }

    @FXML
    private void initialize() {
        nameColumn.setCellValueFactory(cell -> cell.getValue().nameProperty());
        pluginColumn.setCellValueFactory(cell -> cell.getValue().pluginProperty());

        pluginColumn.setCellFactory(new PluginCellFactory());

        ObservableList<StarSurveyModel> list = FXCollections.observableArrayList();
        try {
            starSurveyManager.getAll().forEach(s -> list.add(new StarSurveyModel(s)));
        } catch (ResourceAvailabilityException e) {
            e.printStackTrace();
        }
        starSurveys.setItems(list);
    }

    private void showNoSelectionDialog(){
        // Nothing selected.
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.initOwner(app.getPrimaryStage());
        alert.setTitle("No Selection");
        alert.setHeaderText("No Star Survey Selected");
        alert.setContentText("Please select a person in the table.");

        alert.showAndWait();
    }
}
