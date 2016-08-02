package cz.muni.physics.pdr.app.controller;

import cz.muni.physics.pdr.app.controller.service.CoordsSearchTaskService;
import cz.muni.physics.pdr.app.controller.service.NameSearchTaskService;
import cz.muni.physics.pdr.app.controller.service.StarSurveySearchTaskService;
import cz.muni.physics.pdr.app.javafx.Shaker;
import cz.muni.physics.pdr.app.javafx.SpriteAnimation;
import cz.muni.physics.pdr.app.javafx.control.DecimalTextField;
import cz.muni.physics.pdr.app.javafx.control.TitledTextFieldBox;
import cz.muni.physics.pdr.app.model.RadiusModel;
import cz.muni.physics.pdr.app.model.SearchModel;
import cz.muni.physics.pdr.app.model.StarSurveyModel;
import cz.muni.physics.pdr.app.spring.Screens;
import cz.muni.physics.pdr.app.utils.FXMLUtils;
import cz.muni.physics.pdr.backend.resolver.AvailabilityQueryable;
import javafx.animation.Animation;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

/**
 * @author Michal Krajčovič
 * @version 1.0
 * @since 20/03/16
 */
@Component
public class SearchOverviewController extends StageController {

    private final static Logger logger = LogManager.getLogger(SearchOverviewController.class);

    @Autowired
    private Screens app;
    @Autowired
    private CoordsSearchTaskService coordsSearchTaskService;
    @Autowired
    private NameSearchTaskService nameSearchTaskService;
    @Autowired
    private StarSurveySearchTaskService starSurveySearchService;
    @Autowired
    private Set<AvailabilityQueryable> services;

    @FXML
    private ResourceBundle resources;
    @FXML
    private ImageView image;
    @FXML
    private ChoiceBox<RadiusModel.Unit> radiusUnitChoiceBox;
    @FXML
    private AnchorPane mainPane;
    @FXML
    private Label infoLabel;
    @FXML
    private HBox searchBox;
    @FXML
    private Button searchButton;
    @FXML
    private TitledTextFieldBox searchTextField;
    @FXML
    private DecimalTextField radiusTextField;
    @FXML
    private ProgressIndicator searchProgressIndicator;
    @FXML
    private Label progressLabel;

    private SearchModel searchModel = new SearchModel();
    private SearchQueryParser queryParser;
    private Shaker shaker;
    private Animation imageAnimation;


    @FXML
    private void initialize() {
        app.getPrimaryStage().getScene().setOnKeyPressed(event -> {
            if (event.getCode().equals(KeyCode.ESCAPE)) {
                cancelSearch();
            }
        });
        shaker = new Shaker(mainPane);
        initializeServices();
        initializeFields();
        initializeQueryParser();


        int columns = 15;
        int count = 120;
        imageAnimation = new SpriteAnimation(
                image,
                Duration.millis(10000),
                count, columns,
                0, 0,
                256, 256
        );

        imageAnimation.setCycleCount(Animation.INDEFINITE);
    }

    @FXML
    private void handleSearchButtonAction() {
        disableElements(true);
        services.stream().filter(service -> !service.isAvailable()).forEach(service -> {
            FXMLUtils.alert("Service Unavailable", "Service " + service.getServiceName() + " is not available",
                    "Some features may not work properly. Please try again later.", Alert.AlertType.ERROR).show();
        });
        queryParser.parseQuery(searchModel);
    }

    private void initializeFields() {
        Map<String, String> textFieldChanges = new HashMap<>();
        textFieldChanges.put("name", "Name:");
        textFieldChanges.put("coords", "Coords:");
        textFieldChanges.put("coordinates", "Coords:");
        searchTextField.setAutomaticTitles(":", textFieldChanges);
        searchTextField.textWithPrefixProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isEmpty()) {
                removeErrorInfo();
            }
        });

        radiusUnitChoiceBox.setItems(FXCollections.observableArrayList(RadiusModel.Unit.values()));
        radiusUnitChoiceBox.getSelectionModel().selectFirst();
        searchModel.queryProperty().bindBidirectional(searchTextField.textWithPrefixProperty());
        searchModel.getRadius().unitProperty().bind(radiusUnitChoiceBox.selectionModelProperty().get().selectedItemProperty());
        searchModel.getRadius().radiusProperty().bind(radiusTextField.valueProperty());
    }

    private void initializeQueryParser() {
        queryParser = new SearchQueryParser((model) -> {
            logger.debug("Sending params query={}, radius={} to CoordsSearchService", model);
            coordsSearchTaskService.start();
        }, model -> {
            logger.debug("Sending name {} to NameSearchService", model);
            nameSearchTaskService.start();
        }, error -> {
            showErrorMessage(error);
            disableElements(false);
        }, resources);
    }

    private void initializeServices() {
        initializeCoordsSearchService();
        initializeNameSearchService();
        initializeStarSurveySearchService();
    }

    private void initializeNameSearchService() {
        nameSearchTaskService.setModel(searchModel);
        nameSearchTaskService.setOnError(this::showErrorMessage);
        nameSearchTaskService.setOnDone(() -> disableElements(false));
    }

    private void initializeStarSurveySearchService() {
        starSurveySearchService.setOnError(this::showErrorMessage);
        starSurveySearchService.setOnDone(() -> disableElements(false));
        starSurveySearchService.getStarSurveysMap().addListener((MapChangeListener<StarSurveyModel, Boolean>) change -> {
            if (change.wasAdded())
                Platform.runLater(() -> progressLabel.setText(change.getKey().getName() + "->" + change.getValueAdded()));
        });
    }

    private void initializeCoordsSearchService() {
        coordsSearchTaskService.setModel(searchModel);
        coordsSearchTaskService.setOnError(this::showErrorMessage);
        coordsSearchTaskService.setOnDone(() -> disableElements(false));
    }


    private void disableElements(boolean disable) {
        searchButton.setDisable(disable);
        searchBox.setDisable(disable);
        infoLabel.setVisible(!disable);
        //searchProgressIndicator.setVisible(disable);
        progressLabel.setVisible(disable);
        if (!disable) {
            progressLabel.setText("");
            imageAnimation.pause();
        } else {
            removeErrorInfo();
            imageAnimation.play();
        }
    }

    private void removeErrorInfo() {
        infoLabel.setText("");
        searchTextField.getInnerTextField().getStyleClass().remove("error");
    }

    private void showErrorMessage(String message) {
        shaker.shake();
        searchTextField.getInnerTextField().getStyleClass().add("error");
        infoLabel.setText(message);
        infoLabel.setVisible(true);
    }

    private void cancelSearch() {
        logger.debug("Canceled search");
        if (coordsSearchTaskService.isRunning()) {
            coordsSearchTaskService.cancel();
            coordsSearchTaskService.reset();
        }
        if (nameSearchTaskService.isRunning()) {
            nameSearchTaskService.cancel();
            nameSearchTaskService.reset();
        }
        if (starSurveySearchService.isRunning()) {
            starSurveySearchService.cancel();
            starSurveySearchService.reset();
        }
        disableElements(false);
    }

}
