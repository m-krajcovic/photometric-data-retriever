package cz.muni.physics.pdr.app.controller;

import cz.muni.physics.pdr.app.controller.service.CoordsSearchService;
import cz.muni.physics.pdr.app.controller.service.NameSearchService;
import cz.muni.physics.pdr.app.controller.service.StarSurveySearchService;
import cz.muni.physics.pdr.app.javafx.Shaker;
import cz.muni.physics.pdr.app.javafx.TitledTextField;
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
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
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
    private CoordsSearchService coordsSearchService;
    @Autowired
    private NameSearchService nameSearchService;
    @Autowired
    private StarSurveySearchService starSurveySearchService;


    private StellarObject stellarObject = new StellarObject();

    @FXML
    private AnchorPane mainPane;
    @FXML
    private Label infoLabel;
    @FXML
    private VBox searchBox;
    @FXML
    private Button searchButton;
    @FXML
    private TitledTextField searchTextField;
    @FXML
    private TextField radiusTextField;
    @FXML
    private ProgressIndicator searchProgressIndicator;
    @FXML
    private Label progressLabel;

    private Shaker shaker;

    @FXML
    private void initialize() {
        app.getPrimaryStage().getScene().setOnKeyPressed(event -> {
            if (event.getCode().equals(KeyCode.ESCAPE)) {
                cancelSearch();
            }
        });
        shaker = new Shaker(mainPane);
        radiusTextField.setTextFormatter(new TextFormatter<>(new RadiusFilter()));
        initializeServices();
        searchTextField.setPrimaryPane(mainPane);
        Map<String, String> textFieldChanges = new HashMap<>();
        textFieldChanges.put("name", "Name:");
        textFieldChanges.put("coords", "Coords:");
        textFieldChanges.put("coordinates", "Coords:");
        searchTextField.setAutomaticTitles(":", textFieldChanges);
    }

    @FXML
    private void handleSearchButtonAction() {
        stellarObject = new StellarObject();
        decideSearchMode();
    }

    private void decideSearchMode() {
        String searchText = searchTextField.getTextWithPrefix().trim();
        if (StringUtils.startsWithIgnoreCase(searchText, "name:")) {
            handleNameSearch(searchText.substring(5).trim());
            return;
        }
        if (StringUtils.startsWithIgnoreCase(searchText, "coords:")) {
            handleCoordsSearch(searchText.substring(7).trim());
            return;
        }
        String[] spaceSplit = searchText.split(" ");
        if (spaceSplit.length == 2 && NumberUtils.isParsable(spaceSplit[0]) && NumberUtils.isParsable(spaceSplit[1])) {
            double ra = Double.parseDouble(spaceSplit[0]);
            double dec = Double.parseDouble(spaceSplit[1]);
            if (ra < 0 || ra > 360 || dec < -90 || dec > 90) {
                handleNameSearch(searchText);
                return;
            }
            handleCoordsSearch(searchText);
        } else {
            handleNameSearch(searchText);
        }
    }

    private void handleCoordsSearch(String query) {
        disableElements(true);
        if (query.isEmpty()) {
            showInfoMessage("Query is empty. Insert coords in format '118.77167 +22.00139'");
            disableElements(false);
        } else {
            String[] degrees = query.split(" ");
            if (degrees.length != 2 || !NumberUtils.isParsable(degrees[0]) || !NumberUtils.isParsable(degrees[1])) {
                showInfoMessage("Wrong format use degrees like '118.77167 +22.00139'");
                disableElements(false);
            } else {
                double ra = Double.parseDouble(degrees[0]);
                double dec = Double.parseDouble(degrees[1]);
                double rad;
                if (ra < 0 || ra > 360) {
                    showInfoMessage("Right Ascension must be from interval [0, 360]'");
                    disableElements(false);
                    return;
                } else if (dec < -90 || dec > 90) {
                    showInfoMessage("Declination must be from interval [-90, +90]'");
                    disableElements(false);
                    return;
                }
                if (!NumberUtils.isParsable(radiusTextField.getText()) || radiusTextField.getText().isEmpty()) {
                    radiusTextField.setText("0.05");
                }
                coordsSearchService.setCoords(new CelestialCoordinates(ra, dec, Double.parseDouble(radiusTextField.getText())));
                coordsSearchService.start();
            }
        }
    }

    private void handleNameSearch(String query) {
        disableElements(true);
        if (query.isEmpty()) {
            logger.debug("Query is empty");
            showInfoMessage("Query is empty");
            disableElements(false);
        } else {
            logger.debug("Handling search by name '{}'", query);
            nameSearchService.setSearchText(query);
            nameSearchService.start();
        }
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
            nameSearchService.reset();
        });
    }

    private void initializeStarSurveySearchService() {
        starSurveySearchService.setOnSucceeded(e -> {
            Map<StarSurvey, List<PhotometricDataModel>> data = starSurveySearchService.getValue();
            if (data.isEmpty()) {
                logger.debug("No results found for '{}'", searchTextField.getText());
                showInfoMessage("No results found for '" + searchTextField.getText() + "'");
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
                showInfoMessage("No results found");
                disableElements(false);
            } else {
                selected = app.showStellarObjects(searchResult);
                if (selected != null) {
                    stellarObject.merge(selected.toEntity());
                    nameSearchService.setSearchText(selected.getName());
                    nameSearchService.start();
                } else {
                    disableElements(false);
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


    private void disableElements(boolean disable) {
        searchButton.setDisable(disable);
        searchBox.setDisable(disable);
        infoLabel.setVisible(!disable);
        searchProgressIndicator.setVisible(disable);
        if (!disable) {
            progressLabel.setText("");
        } else {
            infoLabel.setText("");
            searchTextField.getStyleClass().remove("error");
        }
    }

    private void showInfoMessage(String message) {
        shaker.shake();
        searchTextField.getStyleClass().add("error");
        infoLabel.setText(message);
        infoLabel.setVisible(true);
    }

    private void cancelSearch() {
        logger.debug("Canceled search");
        if (coordsSearchService.isRunning()) {
            coordsSearchService.cancel();
            coordsSearchService.reset();
        }
        if (nameSearchService.isRunning()) {
            nameSearchService.cancel();
            nameSearchService.reset();
        }
        if (starSurveySearchService.isRunning()) {
            starSurveySearchService.cancel();
            starSurveySearchService.reset();
        }
        disableElements(false);
    }

}
