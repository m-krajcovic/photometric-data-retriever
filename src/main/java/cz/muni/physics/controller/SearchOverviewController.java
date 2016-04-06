package cz.muni.physics.controller;

import cz.muni.physics.MainApp;
import cz.muni.physics.java.PhotometricData;
import cz.muni.physics.model.DatabaseRecord;
import cz.muni.physics.service.DatabaseSearchService;
import cz.muni.physics.service.SesameService;
import cz.muni.physics.sesame.SesameResult;
import cz.muni.physics.utils.FXMLUtil;
import javafx.collections.MapChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.SplitMenuButton;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
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
    DatabaseSearchService databaseSearchService;

    @FXML
    private SplitMenuButton searchButton;
    @FXML
    private TextField searchTextField;
    @FXML
    private ProgressIndicator searchProgressIndicator;
    @FXML
    private Label progressLabel;

    @FXML
    private void handleSearchButtonAction() {
        toggleElements(true);
        Stage primaryStage = (Stage) searchButton.getScene().getWindow();
        if (searchTextField.getText().isEmpty()) {
            logger.debug("Text field is empty");
            FXMLUtil.showTooltip("Text field is empty", primaryStage, searchTextField);
        } else {
            logger.debug("Handling search by name '{}'", searchTextField.getText());
            sesameService.setSearchText(searchTextField.getText());
            sesameService.setOnSucceeded(e -> {
                logger.debug("Found some stuff");
                SesameResult sesameResult = sesameService.getValue();
                databaseSearchService.setSesameResult(sesameResult);
                databaseSearchService.setDatabaseRecords(mainApp.getDbRecords());

                databaseSearchService.setOnSucceeded(e1 -> {
                    logger.debug("Succeeded");
                    List<PhotometricData> data = databaseSearchService.getValue();
                    if (data.size() == 0) {
                        FXMLUtil.showTooltip("No results found", primaryStage, searchTextField);
                    } else {
                        mainApp.showPhotometricDataOverview(data);
                    }
                    toggleElements(false);
                });

                databaseSearchService.setOnFailed(e1 -> {
                    logger.debug("Failed");
                    toggleElements(false);
                });

                databaseSearchService.getDbRecordMap().addListener((MapChangeListener<DatabaseRecord, Boolean>) change -> {
                    progressLabel.setText(change.getKey().getName() + "->" + change.getValueAdded());
                });

                databaseSearchService.start();

            });
            sesameService.setOnFailed(e -> {
                // TODO announce to client
            });
            sesameService.start();
        }
    }

    private void toggleElements(boolean disabled) {
        searchButton.setDisable(disabled);
        searchTextField.setDisable(disabled);
        searchProgressIndicator.setVisible(disabled);
        progressLabel.setVisible(disabled);
    }

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }
}
