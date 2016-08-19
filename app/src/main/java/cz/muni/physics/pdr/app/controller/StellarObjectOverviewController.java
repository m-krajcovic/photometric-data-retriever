package cz.muni.physics.pdr.app.controller;

import cz.muni.physics.pdr.app.model.StellarObjectModel;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * @author Michal Krajčovič
 * @version 1.0
 * @since 16/04/16
 */
@Component
@Scope("prototype")
public class StellarObjectOverviewController extends StageController {

    private static final Logger logger = LogManager.getLogger(StellarObjectOverviewController.class);

    @FXML
    public TabPane tabPane;
    @FXML
    private ResourceBundle resources;
    @FXML
    private Button selectButton;
    @FXML
    private Button cancelButton;

    private Map<Tab, TableView<StellarObjectModel>> tables = new HashMap<>();

    private StellarObjectModel selectedModel;

    @FXML
    private void initialize() {
//
//        stellarObjectsTable.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
//            if (event.getCode().equals(KeyCode.ENTER)) {
//                handleSelectButton();
//            } else if (event.getCode().equals(KeyCode.ESCAPE)) {
//                handleCancelButton();
//            }
//        });
//
//        Platform.runLater(() -> stellarObjectsTable.requestFocus());
    }

    @FXML
    private void handleCancelButton() {
        selectedModel = null;
        stage.close();
    }

    @FXML
    private void handleSelectButton() {
        TableView<StellarObjectModel> stellarObjectsTable = tables.get(tabPane.getSelectionModel().getSelectedItem());
        selectedModel = stellarObjectsTable.getSelectionModel().getSelectedItem();
        if (selectedModel != null) {
            stage.close();
        }
    }

    public void setItems(Map<String, List<StellarObjectModel>> models) {
        models.forEach((s, stellarObjectModels) -> {
            Tab tab = getTab(s, stellarObjectModels);
            tabPane.getTabs().add(tab);
        });
    }

    private Tab getTab(String name, List<StellarObjectModel> models) {
        TableView<StellarObjectModel> table;
        try {
            table = FXMLLoader.load(this.getClass().getResource("/view/StellarObjectTable.fxml"), resources);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load .fxml resource", e);
        }
        table.getItems().addAll(models);
        Tab tab = new Tab();
        tab.setClosable(false);
        tab.setContent(table);
        tab.setGraphic(new Label(String.format("%s (%d)", name, models.size())));
        tables.put(tab, table);
        return tab;
    }

    @Override
    public void setStage(Stage stage) {
        super.setStage(stage);
        this.stage.setOnCloseRequest(event -> {
            selectedModel = null;
        });
    }

    public StellarObjectModel getSelected() {
        return selectedModel;
    }
}
