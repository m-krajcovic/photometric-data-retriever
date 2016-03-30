package cz.muni.physics.controller;

import cz.muni.physics.plugin.java.JavaPluginManager;
import cz.muni.physics.sesame.SesameClientImpl;
import cz.muni.physics.utils.FXMLUtil;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.SplitMenuButton;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @author Michal Krajčovič
 * @version 1.0
 * @since 20/03/16
 */
public class SearchOverviewController {

    private final static Logger logger = Logger.getLogger(SearchOverviewController.class);

    @Autowired
    private SesameClientImpl sesameClient;

    @Autowired
    private JavaPluginManager javaPluginManager;

    @FXML
    private SplitMenuButton searchButton;
    @FXML
    private TextField searchTextField;
    @FXML
    private ProgressIndicator searchProgressIndicator;
    @FXML
    private Label progressLabel;

    public SearchOverviewController() {

    }

    @FXML
    private void handleSearchButtonAction() {
        toggleElements(true);
        Stage primaryStage = (Stage) searchButton.getScene().getWindow();
        if (searchTextField.getText().isEmpty()) {
            logger.debug("Text field is empty");
            FXMLUtil.showTooltip("Text field is empty", primaryStage, searchTextField);
        } else {
            logger.debug("Handling search by name '" + searchTextField.getText() + "'.");
            Task<List<String>> task = new Task<List<String>>() {
                @Override
                protected List<String> call() throws Exception {
                    return sesameClient.getData(searchTextField.getText()).getNames();
                }
            };

            task.setOnSucceeded(e -> {
                List<String> names = task.getValue();
                if (names.size() == 0) {
                    FXMLUtil.showTooltip("No results found", primaryStage, searchTextField);
                } else {
                    javaPluginManager.startPlugins(names).forEach(d -> System.out.println(d.getJulianDate() + ", " + d.getMagnitude()));
                }
                toggleElements(false);
            });

            task.setOnFailed(e -> {
                toggleElements(false);
            });

            new Thread(task).start();
        }
    }

    private void toggleElements(boolean disabled) {
        searchButton.setDisable(disabled);
        searchTextField.setDisable(disabled);
        searchProgressIndicator.setVisible(disabled);
    }
}
