package cz.muni.physics.controller;

import cz.muni.physics.model.StarSurvey;
import cz.muni.physics.utils.AppConfig;
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
    private AppConfig app;

    @FXML
    private TableView<StarSurvey> starSurveys;
    @FXML
    private TableColumn<StarSurvey, String> nameColumn;
    @FXML
    private TableColumn<StarSurvey, String> sesameIdentifierColumn;
    @FXML
    private TableColumn<StarSurvey, String> urlColumn;
    @FXML
    private TableColumn<StarSurvey, String> pluginColumn;
    @FXML
    private Button button;

    public StarSurveyOverviewController() {

    }

    @FXML
    private void handleNewButton() {
        StarSurvey tempStarSurvey = new StarSurvey();
        boolean okClicked = app.showStarSurveyEditDialog(tempStarSurvey);
        if (okClicked) {
            app.getStarSurveys().add(tempStarSurvey);
        }
    }

    @FXML
    private void handleEditButton() {
        StarSurvey selectedRecord = starSurveys.getSelectionModel().getSelectedItem();
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
        pluginColumn.setCellValueFactory(cell -> cell.getValue().getPlugin().nameProperty());

        starSurveys.setItems(app.getStarSurveys());
    }
}
