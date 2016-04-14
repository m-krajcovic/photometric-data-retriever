package cz.muni.physics.pdr.controller;

import cz.muni.physics.pdr.javafx.PluginCellFactory;
import cz.muni.physics.pdr.model.PluginModel;
import cz.muni.physics.pdr.model.StarSurveyModel;
import cz.muni.physics.pdr.utils.ScreenConfig;
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
            app.getStarSurveyModels().add(tempStarSurvey);
        }
    }

    @FXML
    private void handleEditButton() {
        StarSurveyModel selectedRecord = starSurveys.getSelectionModel().getSelectedItem();
        if (selectedRecord != null) {
            boolean okClicked = app.showStarSurveyEditDialog(selectedRecord);
            if (okClicked) {
                starSurveys.refresh();
            }
        } else {
            // Nothing selected.
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.initOwner(app.getPrimaryStage());
            alert.setTitle("No Selection");
            alert.setHeaderText("No Star Survey Selected");
            alert.setContentText("Please select a person in the table.");

            alert.showAndWait();
        }
    }

    @FXML
    private void handleDeleteButton() {

    }

    @FXML
    private void initialize() {
        nameColumn.setCellValueFactory(cell -> cell.getValue().nameProperty());
        pluginColumn.setCellValueFactory(cell -> cell.getValue().pluginProperty());

        pluginColumn.setCellFactory(new PluginCellFactory());

        starSurveys.setItems(app.getStarSurveyModels());
    }
}
