package cz.muni.physics.pdr.app.controller;

import cz.muni.physics.pdr.app.controller.service.CoordsSearchService;
import cz.muni.physics.pdr.app.controller.service.NameSearchService;
import cz.muni.physics.pdr.app.controller.service.StarSurveySearchService;
import cz.muni.physics.pdr.app.javafx.Shaker;
import cz.muni.physics.pdr.app.javafx.TitledTextFieldBox;
import cz.muni.physics.pdr.app.javafx.formatter.RadiusFilter;
import cz.muni.physics.pdr.app.model.PhotometricDataModel;
import cz.muni.physics.pdr.app.model.StellarObjectModel;
import cz.muni.physics.pdr.app.utils.FXMLUtils;
import cz.muni.physics.pdr.app.utils.ScreenConfig;
import cz.muni.physics.pdr.backend.entity.StarSurvey;
import cz.muni.physics.pdr.backend.entity.StellarObject;
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

    @FXML
    private AnchorPane mainPane;
    @FXML
    private Label infoLabel;
    @FXML
    private VBox searchBox;
    @FXML
    private Button searchButton;
    @FXML
    private TitledTextFieldBox searchTextField;
    @FXML
    private TextField radiusTextField;
    @FXML
    private ProgressIndicator searchProgressIndicator;
    @FXML
    private Label progressLabel;

    private StellarObject stellarObject = new StellarObject();
    private SearchQueryParser queryParser;
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
        initializeTitledTextField();
        initializeQueryParser();
    }

    @FXML
    private void handleSearchButtonAction() {
        stellarObject = new StellarObject();
        disableElements(true);
        queryParser.parseQuery(searchTextField.getTextWithPrefix(), radiusTextField.getText());
    }

    private void initializeTitledTextField() {
        Map<String, String> textFieldChanges = new HashMap<>();
        textFieldChanges.put("name", "Name:");
        textFieldChanges.put("coords", "Coords:");
        textFieldChanges.put("coordinates", "Coords:");
        searchTextField.setAutomaticTitles(":", textFieldChanges);
        searchTextField.getTextField().textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isEmpty()) {
                removeErrorInfo();
            }
        });
    }

    private void initializeQueryParser() {
        queryParser = new SearchQueryParser(coordinates -> {
            coordsSearchService.setCoords(coordinates);
            coordsSearchService.start();
        }, name -> {
            nameSearchService.setSearchText(name);
            nameSearchService.start();
        }, error -> {
            showErrorMessage(error);
            disableElements(false);
        });
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
            FXMLUtils.alert("Error", null, "Failed to get data from Name Resolvers", Alert.AlertType.ERROR);
            nameSearchService.reset();
            disableElements(false);
            nameSearchService.reset();
        });
    }

    private void initializeStarSurveySearchService() {
        starSurveySearchService.setOnSucceeded(e -> {
            Map<StarSurvey, List<PhotometricDataModel>> data = starSurveySearchService.getValue();
            if (data.isEmpty()) {
                logger.debug("No results found for '{}'", searchTextField.getTextWithPrefix());
                showErrorMessage("No results found for '" + searchTextField.getTextWithPrefix() + "'");
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
                showErrorMessage("No results found");
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
            FXMLUtils.alert("Error", null, "Failed to get data from Coords Resolvers", Alert.AlertType.ERROR);
            disableElements(false);
            coordsSearchService.reset();
        });
    }


    private void disableElements(boolean disable) {
        searchButton.setDisable(disable);
        searchBox.setDisable(disable);
        infoLabel.setVisible(!disable);
        searchProgressIndicator.setVisible(disable);
        progressLabel.setVisible(disable);
        if (!disable) {
            progressLabel.setText("");
        } else {
            removeErrorInfo();
        }
    }

    private void removeErrorInfo() {
        infoLabel.setText("");
        searchTextField.getTextField().getStyleClass().remove("error");
    }

    private void showErrorMessage(String message) {
        shaker.shake();
        searchTextField.getTextField().getStyleClass().add("error");
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
