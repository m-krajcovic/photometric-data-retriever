package cz.muni.physics.pdr.app.controller;

import cz.muni.physics.pdr.app.controller.service.CoordsSearchService;
import cz.muni.physics.pdr.app.controller.service.NameSearchService;
import cz.muni.physics.pdr.app.controller.service.StarSurveySearchService;
import cz.muni.physics.pdr.app.javafx.formatter.CoordsFilter;
import cz.muni.physics.pdr.app.javafx.formatter.RadiusFilter;
import cz.muni.physics.pdr.app.model.PhotometricDataModel;
import cz.muni.physics.pdr.app.model.StellarObjectModel;
import cz.muni.physics.pdr.app.utils.FXMLUtils;
import cz.muni.physics.pdr.app.utils.ScreenConfig;
import cz.muni.physics.pdr.backend.entity.CelestialCoordinates;
import cz.muni.physics.pdr.backend.entity.StarSurvey;
import cz.muni.physics.pdr.backend.entity.StellarObject;
import cz.muni.physics.pdr.backend.utils.NumberUtils;
import javafx.application.Platform;
import javafx.collections.MapChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.SplitMenuButton;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.HBox;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
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
    private ScreenConfig app;
    @Autowired
    private NameSearchService nameSearchService;
    @Autowired
    private StarSurveySearchService starSurveySearchService;
    @Autowired
    private ThreadPoolTaskExecutor searchServiceExecutor;
    @Autowired
    private CoordsSearchService coordsSearchService;


    private StellarObject stellarObject = new StellarObject();

    @FXML
    private MenuItem byNameToggler;
    @FXML
    private MenuItem byCoordsToggler;
    @FXML
    private HBox nameSearchBox;
    @FXML
    private HBox coordsSearchBox;
    @FXML
    private SplitMenuButton searchButton;
    @FXML
    private TextField nameTextField;
    @FXML
    private TextField degreesTextField;
    @FXML
    private TextField radiusTextField;
    @FXML
    private ProgressIndicator searchProgressIndicator;
    @FXML
    private Label progressLabel;

    private SearchMode searchMode;

    @FXML
    private void initialize() {
        degreesTextField.setTextFormatter(new TextFormatter<>(new CoordsFilter()));
        radiusTextField.setTextFormatter(new TextFormatter<>(new RadiusFilter()));
        initializeServices();
        setNameSearchMode();
    }

    @FXML
    private void handleSearchButtonAction() {
        disableElements(true);
        stellarObject = new StellarObject();
        if (searchMode.equals(SearchMode.NAME)) {
            if (nameTextField.getText().isEmpty()) {
                logger.debug("Text field is empty");
                FXMLUtils.showTooltip("Text field is empty", searchButton.getScene().getWindow(), nameTextField);
                disableElements(false);
            } else {
                logger.debug("Handling search by name '{}'", nameTextField.getText());
                nameSearchService.setSearchText(nameTextField.getText());
                nameSearchService.start();
            }
        } else if (searchMode.equals(SearchMode.COORDS)) {
            if (degreesTextField.getText().isEmpty()) {
                FXMLUtils.showTooltip("Text field is empty. Insert coords in format '118.77167 +22.00139'", searchButton.getScene().getWindow(), degreesTextField);
                disableElements(false);
            } else {
                String[] degrees = degreesTextField.getText().split(" ");
                if (degrees.length != 2 || !NumberUtils.isParsable(degrees[0]) || !NumberUtils.isParsable(degrees[1])) {
                    FXMLUtils.showTooltip("Wrong format use degrees like '118.77167 +22.00139'", searchButton.getScene().getWindow(), degreesTextField);
                    disableElements(false);
                } else {
                    double ra = Double.parseDouble(degrees[0]);
                    double dec = Double.parseDouble(degrees[1]);
                    if (ra < 0 || ra > 360) {
                        FXMLUtils.showTooltip("Right Ascension must be from interval [0, 360]'", searchButton.getScene().getWindow(), degreesTextField);
                    }
                    if (dec < -90 || dec > 90) {
                        FXMLUtils.showTooltip("Declination must be from interval [-90, +90]'", searchButton.getScene().getWindow(), degreesTextField);
                    }
                    if (!NumberUtils.isParsable(radiusTextField.getText()) || radiusTextField.getText().isEmpty()) {
                        radiusTextField.setText("0.05");
                    }
                    coordsSearchService.setCoords(new CelestialCoordinates(ra, dec, Double.parseDouble(radiusTextField.getText())));
                    coordsSearchService.start();
                }
            }
        }
    }

    private void disableElements(boolean disable) {
        searchButton.setDisable(disable);
        coordsSearchBox.setDisable(disable);
        nameSearchBox.setDisable(disable);
        searchProgressIndicator.setVisible(disable);
        progressLabel.setVisible(disable);
        if (!disable) {
            progressLabel.setText("");
        }
    }

    @FXML
    private void setCoordsSearchMode() {
        searchMode = SearchMode.COORDS;

        nameSearchBox.setDisable(true);
        nameSearchBox.setVisible(false);

        coordsSearchBox.setDisable(false);
        coordsSearchBox.setVisible(true);
    }

    @FXML
    private void setNameSearchMode() {
        searchMode = SearchMode.NAME;

        coordsSearchBox.setDisable(true);
        coordsSearchBox.setVisible(false);

        nameSearchBox.setDisable(false);
        nameSearchBox.setVisible(true);
    }

    private void initializeServices() {
        initializeCoordsSearchService();
        initializeNameSearchService();
        initializeStarSurveySearchService();
    }

    private void initializeNameSearchService() {
        nameSearchService.setOnSucceeded(e -> {
            logger.debug("Succeeded in retrieving star resolver data.");
            stellarObject.merge(nameSearchService.getValue());
            starSurveySearchService.setResolverResult(stellarObject);
            starSurveySearchService.start();

            nameSearchService.reset();
        });
        nameSearchService.setOnFailed(e -> {
            logger.error("Failed to get data from Name Resolvers", nameSearchService.getException());
            FXMLUtils.showAlert("Error", null, "Failed to get data from Name Resolvers", Alert.AlertType.ERROR);
            nameSearchService.reset();
            disableElements(false);
        });
    }

    private void initializeStarSurveySearchService() {
        starSurveySearchService.setOnSucceeded(e -> {
            Map<StarSurvey, List<PhotometricDataModel>> data = starSurveySearchService.getValue();
            if (data.isEmpty()) {
                logger.debug("No results found for '{}'", nameTextField.getText());
                FXMLUtils.showTooltip("No results found", searchButton.getScene().getWindow(), nameTextField);
            } else {
                app.showPhotometricDataOverview(data, stellarObject);
            }
            starSurveySearchService.reset();
            disableElements(false);
        });
        starSurveySearchService.setOnFailed(e -> {
            logger.debug("Failed", starSurveySearchService.getException());
            starSurveySearchService.reset();
            disableElements(false);
        });
        starSurveySearchService.getStarSurveysMap().addListener((MapChangeListener<StarSurvey, Boolean>) change -> {
            if (change.wasAdded())
                Platform.runLater(() -> progressLabel.setText(change.getKey().getName() + "->" + change.getValueAdded()));
        });
    }

    private void initializeCoordsSearchService() {
        coordsSearchService.setOnSucceeded(event -> {
            StellarObjectModel selected;
            List<StellarObjectModel> searchResult = coordsSearchService.getValue();
            if (searchResult.isEmpty()) {
                FXMLUtils.showTooltip("No results found", searchButton.getScene().getWindow(), degreesTextField);
                disableElements(false);
            } else {
                selected = app.showStellarObjects(searchResult, e -> disableElements(false));
                if (selected != null) {
                    nameSearchService.setSearchText(selected.getName());
                    nameSearchService.start();
                }
            }
            coordsSearchService.reset();
        });

        coordsSearchService.setOnFailed(event -> {
            logger.error("Failed to get data from Coords Resolvers", coordsSearchService.getException());
            FXMLUtils.showAlert("Error", null, "Failed to get data from Coords Resolvers", Alert.AlertType.ERROR);
            disableElements(false);
            coordsSearchService.reset();
        });
    }


    private enum SearchMode {
        NAME, COORDS
    }

}
