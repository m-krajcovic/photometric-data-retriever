package cz.muni.physics.controller;

import cz.muni.physics.MainApp;
import cz.muni.physics.java.PhotometricData;
import cz.muni.physics.model.StarSurvey;
import cz.muni.physics.service.StarSurveySearchService;
import cz.muni.physics.service.SesameService;
import cz.muni.physics.sesame.SesameResult;
import cz.muni.physics.utils.FXMLUtil;
import javafx.collections.MapChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @author Michal Krajčovič
 * @version 1.0
 * @since 20/03/16
 */
public class SearchOverviewController {

    private final static Logger logger = LogManager.getLogger(SearchOverviewController.class);

    private MainApp mainApp;

    @Autowired
    SesameService sesameService;
    @Autowired
    StarSurveySearchService starSurveySearchService;

    @FXML
    private SplitMenuButton searchButton;
    @FXML
    private TextField searchTextField;
    @FXML
    private ProgressIndicator searchProgressIndicator;
    @FXML
    private Label progressLabel;

    @FXML
    private void initialize() {
        sesameService.setOnSucceeded(e -> {
            SesameResult sesameResult = sesameService.getValue();
            starSurveySearchService.setSesameResult(sesameResult);
            starSurveySearchService.setStarSurveys(mainApp.getStarSurveys());

            starSurveySearchService.start();
            sesameService.reset();
        });
        sesameService.setOnFailed(e -> {
            logger.error("Failed to get data from Sesame Name Resolver");
            FXMLUtil.showAlert("Error", null, "Failed to get data from Sesame Name Resolver", Alert.AlertType.ERROR);
            sesameService.reset();
            toggleElements(false);
        });

        starSurveySearchService.setOnSucceeded(e -> {
            List<PhotometricData> data = starSurveySearchService.getValue();
            if (data.size() == 0) {
                FXMLUtil.showTooltip("No results found", searchButton.getScene().getWindow(), searchTextField);
            } else {
                mainApp.showPhotometricDataOverview(data);
            }
            starSurveySearchService.reset();
            toggleElements(false);
        });
        starSurveySearchService.setOnFailed(e -> {
            logger.debug("Failed");
            starSurveySearchService.reset();
            toggleElements(false);
        });
        starSurveySearchService.getStarSurveysMap().addListener((MapChangeListener<StarSurvey, Boolean>) change -> {
            if (change.wasAdded())
                progressLabel.setText(change.getKey().getName() + "->" + change.getValueAdded());
        });
    }

    @FXML
    private void handleSearchButtonAction() {
        toggleElements(true);
        if (searchTextField.getText().isEmpty()) {
            logger.debug("Text field is empty");
            FXMLUtil.showTooltip("Text field is empty", searchButton.getScene().getWindow(), searchTextField);
            toggleElements(false);
        } else {
            logger.debug("Handling search by name '{}'", searchTextField.getText());
            sesameService.setSearchText(searchTextField.getText());
            sesameService.start();
        }
    }

    private void toggleElements(boolean disabled) {
        searchButton.setDisable(disabled);
        searchTextField.setDisable(disabled);
        searchProgressIndicator.setVisible(disabled);
        progressLabel.setVisible(disabled);
        if(!disabled){
            progressLabel.setText("");
        }
    }

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }
}
