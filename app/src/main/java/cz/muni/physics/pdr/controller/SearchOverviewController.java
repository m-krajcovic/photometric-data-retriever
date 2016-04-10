package cz.muni.physics.pdr.controller;

import cz.muni.physics.pdr.java.PhotometricData;
import cz.muni.physics.pdr.model.StarSurvey;
import cz.muni.physics.pdr.nameresolver.NameResolverResult;
import cz.muni.physics.pdr.service.NameResolverService;
import cz.muni.physics.pdr.service.StarSurveySearchService;
import cz.muni.physics.pdr.utils.AppConfig;
import cz.muni.physics.pdr.utils.FXMLUtils;
import javafx.collections.MapChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.SplitMenuButton;
import javafx.scene.control.TextField;
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
    private NameResolverService nameResolverService;
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
        nameResolverService.setOnSucceeded(e -> {
            NameResolverResult nameResolverResult = nameResolverService.getValue();
            starSurveySearchService.setNameResolverResult(nameResolverResult);
            starSurveySearchService.setStarSurveys(app.getStarSurveys());

            starSurveySearchService.start();
            nameResolverService.reset();
        });
        nameResolverService.setOnFailed(e -> {
            logger.error("Failed to get data from Sesame Name Resolver");
            FXMLUtils.showAlert("Error", null, "Failed to get data from Sesame Name Resolver", Alert.AlertType.ERROR);
            nameResolverService.reset();
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
            nameResolverService.setSearchText(searchTextField.getText());
            nameResolverService.start();
        }
    }

    private void toggleElements(boolean disabled) {
        searchButton.setDisable(disabled);
        searchTextField.setDisable(disabled);
        searchProgressIndicator.setVisible(disabled);
        progressLabel.setVisible(disabled);
        if (!disabled) {
            progressLabel.setText("");
        }
    }

}
