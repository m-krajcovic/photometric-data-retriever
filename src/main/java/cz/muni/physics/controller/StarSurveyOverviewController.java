package cz.muni.physics.controller;

import cz.muni.physics.MainApp;
import cz.muni.physics.model.StarSurvey;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

/**
 * @author Michal Krajčovič
 * @version 1.0
 * @since 31/03/16
 */
public class StarSurveyOverviewController {

    private MainApp mainApp;

    @FXML
    private TableView<StarSurvey> starSurveyTableView;
    @FXML
    private TableColumn<StarSurvey, String> starSurveyName;
    @FXML
    private TableColumn<StarSurvey, String> starSurveySesameIdentifier;
    @FXML
    private TableColumn<StarSurvey, String> starSurveyURL;
    @FXML
    private TableColumn<StarSurvey, String> starSurveyPlugin;
    @FXML
    private Button button;

    @FXML
    private void handleNewButton() {
        StarSurvey tempStarSurvey = new StarSurvey("", "", null, "");
        boolean okClicked = mainApp.showStarSurveyEditDialog(tempStarSurvey);
        if (okClicked) {
            mainApp.getStarSurveys().add(tempStarSurvey);
        }
    }

    @FXML
    private void handleEditButton() {
        StarSurvey selectedRecord = starSurveyTableView.getSelectionModel().getSelectedItem();
        if (selectedRecord != null) {
            boolean okClicked = mainApp.showStarSurveyEditDialog(selectedRecord);
            if (okClicked) {
                starSurveyTableView.refresh();
            }
        } else {
            // Nothing selected.
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.initOwner(mainApp.getPrimaryStage());
            alert.setTitle("No Selection");
            alert.setHeaderText("No Database Record Selected");
            alert.setContentText("Please select a person in the table.");

            alert.showAndWait();
        }
    }

    @FXML
    private void handleDeleteButton() {

    }

    @FXML
    private void initialize() {
        starSurveyName.setCellValueFactory(cell -> cell.getValue().nameProperty());
        starSurveySesameIdentifier.setCellValueFactory(cell -> cell.getValue().sesameAliasProperty());
        starSurveyURL.setCellValueFactory(cell -> cell.getValue().URLProperty());
        starSurveyPlugin.setCellValueFactory(cell -> cell.getValue().getPlugin().nameProperty());

    }

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
        starSurveyTableView.setItems(mainApp.getStarSurveys());
    }
}
