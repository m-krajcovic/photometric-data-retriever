package cz.muni.physics.controller;

import cz.muni.physics.plugin.java.JavaPluginManager;
import cz.muni.physics.sesame.SesameClientImpl;
import cz.muni.physics.utils.FXMLUtil;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.ResourceAccessException;

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
            FXMLUtil.INSTANCE.showTooltip("Text field is empty", primaryStage, searchTextField);
        } else {
            logger.debug("Handling search by name '" + searchTextField.getText() + "'.");
            List<String> names;
            try {
                names = sesameClient.getAliases(searchTextField.getText()).getNames();
            } catch(ResourceAccessException exc){
                FXMLUtil.INSTANCE.showAlert("Error!", "Something is wrong", "Seriously fucked up", Alert.AlertType.ERROR);
                toggleElements(false);
                return;
            }
            if (names.size() == 0) {
                FXMLUtil.INSTANCE.showTooltip("No results found", primaryStage, searchTextField);
            } else {
                javaPluginManager.startPlugins(names).forEach(d -> System.out.println(d.getJulianDate() + ", " + d.getMagnitude()));
//                SpringFxmlLoader loader = new SpringFxmlLoader();
//                AnchorPane pane = (AnchorPane) loader.load("/view/SearchResult.fxml");
//                SearchResultController controller = loader.getLastLoader().getController();
//                controller.setAliases(FXCollections.observableArrayList(names));
//                FXMLUtil.INSTANCE.createDialogStage("Search Result", pane, primaryStage);
            }
        }
        toggleElements(false);
    }

    private void toggleElements(boolean disabled) {
        searchButton.setDisable(disabled);
        searchTextField.setDisable(disabled);
        searchProgressIndicator.setVisible(disabled);
    }
}
