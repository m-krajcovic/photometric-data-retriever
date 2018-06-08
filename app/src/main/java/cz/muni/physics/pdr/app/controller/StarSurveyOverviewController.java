package cz.muni.physics.pdr.app.controller;

import cz.muni.physics.pdr.app.javafx.cell.CheckBoxCellFactory;
import cz.muni.physics.pdr.app.javafx.cell.PluginCellFactory;
import cz.muni.physics.pdr.app.model.PluginModel;
import cz.muni.physics.pdr.app.model.StarSurveyModel;
import cz.muni.physics.pdr.app.spring.Screens;
import cz.muni.physics.pdr.app.utils.FXMLUtils;
import cz.muni.physics.pdr.backend.exception.ResourceAvailabilityException;
import cz.muni.physics.pdr.backend.manager.StarSurveyManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;

/**
 * @author Michal Krajčovič
 * @version 1.0
 * @since 31/03/16
 */
@Component
public class StarSurveyOverviewController extends StageController {

    private static final Logger logger = LogManager.getLogger(StarSurveyOverviewController.class);

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
    private TableColumn<StarSurveyModel, Boolean> enabledColumn;

    @FXML
    private void initialize() {
        nameColumn.setCellValueFactory(cell -> cell.getValue().nameProperty());
        enabledColumn.setCellValueFactory(cell -> cell.getValue().enabledProperty());

        enabledColumn.setCellFactory(new CheckBoxCellFactory<>());

        ObservableList<StarSurveyModel> list = FXCollections.observableArrayList();
        try {
            starSurveyManager.getAll().forEach(s -> {
                StarSurveyModel e = new StarSurveyModel(s);
                e.enabledProperty().addListener((observable, oldValue, newValue) -> starSurveyManager.insert(e.toEntity()));
                list.add(e);
            });
        } catch (ResourceAvailabilityException e) {
            logger.error(e);
            errorAlert();
        }
        starSurveys.setItems(list);
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
