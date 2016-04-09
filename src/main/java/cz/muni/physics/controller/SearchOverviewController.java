package cz.muni.physics.controller;

import cz.muni.physics.java.PhotometricData;
import cz.muni.physics.model.StarSurvey;
import cz.muni.physics.service.SesameService;
import cz.muni.physics.service.StarSurveySearchService;
import cz.muni.physics.sesame.SesameResult;
import cz.muni.physics.utils.AppConfig;
import cz.muni.physics.utils.FXMLUtils;
import javafx.collections.MapChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @author Michal Krajčovič
 * @version 1.0
 * @since 20/03/16
 */
@Component
public class SearchOverviewController {

    private final static Logger logger = LogManager.getLogger(SearchOverviewController.class);

    @Autowired
    private AppConfig app;
    @Autowired
    private SesameService sesameService;
    @Autowired
    private StarSurveySearchService starSurveySearchService;

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
            starSurveySearchService.setStarSurveys(app.getStarSurveys());

            starSurveySearchService.start();
            sesameService.reset();
        });
        sesameService.setOnFailed(e -> {
            logger.error("Failed to get data from Sesame Name Resolver");
            FXMLUtils.showAlert("Error", null, "Failed to get data from Sesame Name Resolver", Alert.AlertType.ERROR);
            sesameService.reset();
            toggleElements(false);
        });

        starSurveySearchService.setOnSucceeded(e -> {
            Map<StarSurvey, List<PhotometricData>> data = starSurveySearchService.getValue();
            if (data.size() == 0) {
                logger.debug("No results found for '{}'", searchTextField.getText());
                FXMLUtils.showTooltip("No results found", searchButton.getScene().getWindow(), searchTextField);
            } else {
                app.showPhotometricDataOverview(data);
            }
            starSurveySearchService.reset();
            toggleElements(false);
        });
        starSurveySearchService.setOnFailed(e -> {
            logger.debug("Failed"); // TODO any plugin can fail -> this shouldn't affect other plugins
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
            FXMLUtils.showTooltip("Text field is empty", searchButton.getScene().getWindow(), searchTextField);
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

}
