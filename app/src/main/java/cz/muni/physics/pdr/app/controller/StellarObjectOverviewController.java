package cz.muni.physics.pdr.app.controller;

import cz.muni.physics.pdr.app.model.StellarObjectModel;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;

/**
 * @author Michal Krajčovič
 * @version 1.0
 * @since 16/04/16
 */
@Component
@Scope("prototype")
public class StellarObjectOverviewController {

    private final static Logger logger = LogManager.getLogger(StellarObjectOverviewController.class);

    @FXML
    private Button selectButton;
    @FXML
    private TableView<StellarObjectModel> stellarObjectsTable;
    @FXML
    private Button cancelButton;

    @FXML
    private TableColumn<StellarObjectModel, Number> distanceColumn;
    @FXML
    private TableColumn<StellarObjectModel, String> nameColumn;
    @FXML
    private TableColumn<StellarObjectModel, Number> raColumn;
    @FXML
    private TableColumn<StellarObjectModel, Number> decColumn;

    private StellarObjectModel selectedModel;

    private Stage dialogStage;

    @FXML
    private void initialize() {
        selectButton.disableProperty().bind(stellarObjectsTable.getSelectionModel().selectedItemProperty().isNull());
        distanceColumn.setCellValueFactory(cell -> cell.getValue().distanceProperty());
        distanceColumn.setCellFactory(param -> new TableCell<StellarObjectModel, Number>() {
            @Override
            protected void updateItem(Number item, boolean empty) {
                if (item != null) {
                    NumberFormat formatter = new DecimalFormat("#0.00");
                    setText(formatter.format(item));
                }
            }
        });
        nameColumn.setCellValueFactory(cell -> cell.getValue().nameProperty());
        raColumn.setCellValueFactory(cell -> cell.getValue().rightAscensionProperty());
        decColumn.setCellValueFactory(cell -> cell.getValue().declinationProperty());

        stellarObjectsTable.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode().equals(KeyCode.ENTER)) {
                handleSelectButton();
            } else if (event.getCode().equals(KeyCode.ESCAPE)) {
                handleCancelButton();
            }
        });

        stellarObjectsTable.requestFocus();
    }

    @FXML
    private void handleCancelButton() {
        selectedModel = null;
        dialogStage.close();
    }

    @FXML
    private void handleSelectButton() {
        selectedModel = stellarObjectsTable.getSelectionModel().getSelectedItem();
        if (selectedModel != null) {
            dialogStage.close();
        }
    }

    public void setItems(List<StellarObjectModel> models) {
        stellarObjectsTable.setItems(FXCollections.observableArrayList(models));
        distanceColumn.setSortType(TableColumn.SortType.ASCENDING);
        stellarObjectsTable.getSortOrder().add(distanceColumn);
        stellarObjectsTable.getSelectionModel().selectFirst();
    }

    public Stage getDialogStage() {
        return dialogStage;
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
        dialogStage.setOnCloseRequest(event -> {
            selectedModel = null;
        });
    }

    public StellarObjectModel getSelected() {
        return selectedModel;
    }
}
