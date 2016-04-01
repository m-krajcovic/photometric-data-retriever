package cz.muni.physics.controller;

import cz.muni.physics.MainApp;
import cz.muni.physics.model.DatabaseRecord;
import cz.muni.physics.plugin.java.JavaPluginLoader;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Michal Krajčovič
 * @version 1.0
 * @since 31/03/16
 */
public class DatabaseOverviewController {

    private MainApp mainApp;

    @Autowired
    private JavaPluginLoader javaPluginLoader;

    @FXML
    private TableView<DatabaseRecord> dbTableView;
    @FXML
    private TableColumn<DatabaseRecord, String> dbNameColumn;
    @FXML
    private TableColumn<DatabaseRecord, String> dbSesameIdentifier;
    @FXML
    private TableColumn<DatabaseRecord, String> dbURLColumn;
    @FXML
    private TableColumn<DatabaseRecord, String> dbPluginNameColumn;
    @FXML
    private Button button;

    private void handleButton() {
//        DatabaseRecord selected = dbTableView.getSelectionModel().getSelectedItem();
//        try {
//            String objId = textField.getText().isEmpty() ? "7622769" : textField.getText();
//            String url = MessageFormat.format(selected.getURL(), objId);
//            Process process = Runtime.getRuntime().exec(selected.getPlugin().getFullCommand(url));
//            InputStream is = process.getInputStream();
//            InputStreamReader isr = new InputStreamReader(is);
//            BufferedReader buff = new BufferedReader(isr);
//
//            String line;
//            while ((line = buff.readLine()) != null){
//                String[] split = line.split(",");
//                PhotometricData data = new PhotometricData(split[0], split[1], split[2]);
//                System.out.println(data);
//            }
//        } catch (IOException e) {
//            System.out.println("fuck this shit");
//            e.printStackTrace();
//        }
    }

    @FXML
    private void handleNewButton() {
        DatabaseRecord tempDb = new DatabaseRecord("", "", null, "");
        boolean okClicked = mainApp.showDatabaseEditDialog(tempDb);
        if (okClicked) {
            mainApp.getDbRecords().add(tempDb);
        }
    }

    @FXML
    private void handleEditButton() {
        DatabaseRecord selectedRecord = dbTableView.getSelectionModel().getSelectedItem();
        if (selectedRecord != null) {
            boolean okClicked = mainApp.showDatabaseEditDialog(selectedRecord);
            if (okClicked) {
                //showPersonDetails(selectedRecord);
            }
        } else {
            // Nothing selected.
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.initOwner(mainApp.getPrimaryStage());
            alert.setTitle("No Selection");
            alert.setHeaderText("No Person Selected");
            alert.setContentText("Please select a person in the table.");

            alert.showAndWait();
        }
    }

    @FXML
    private void handleDeleteButton() {

    }

    @FXML
    private void initialize() {
        dbNameColumn.setCellValueFactory(cell -> cell.getValue().nameProperty());
        dbSesameIdentifier.setCellValueFactory(cell -> cell.getValue().sesameAliasProperty());
        dbURLColumn.setCellValueFactory(cell -> cell.getValue().URLProperty());
        dbPluginNameColumn.setCellValueFactory(cell -> cell.getValue().getPlugin().nameProperty());

    }

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
        dbTableView.setItems(mainApp.getDbRecords());
    }
}
