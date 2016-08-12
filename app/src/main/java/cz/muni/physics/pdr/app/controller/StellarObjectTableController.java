package cz.muni.physics.pdr.app.controller;

import cz.muni.physics.pdr.app.model.StellarObjectModel;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

/**
 * @author Michal
 * @version 1.0
 * @since 8/12/2016
 */
public class StellarObjectTableController {

    @FXML
    private TableView<StellarObjectModel> stellarObjectsTable;
    @FXML
    private TableColumn<StellarObjectModel, Number> distanceColumn;
    @FXML
    private TableColumn<StellarObjectModel, String> nameColumn;
    @FXML
    private TableColumn<StellarObjectModel, String> raColumn;
    @FXML
    private TableColumn<StellarObjectModel, String> decColumn;

    @FXML
    private void initialize() {
        distanceColumn.setCellValueFactory(cell -> cell.getValue().distanceProperty());
        distanceColumn.setCellFactory(param -> new TableCell<StellarObjectModel, Number>() {
            @Override
            protected void updateItem(Number item, boolean empty) {
                if (item != null) {
                    NumberFormat formatter = new DecimalFormat("#0.00", DecimalFormatSymbols.getInstance(Locale.ENGLISH));
                    setText(formatter.format(item));
                }
            }
        });
        nameColumn.setCellValueFactory(cell -> cell.getValue().nameProperty());
        raColumn.setCellValueFactory(cell -> cell.getValue().rightAscensionProperty());
        decColumn.setCellValueFactory(cell -> cell.getValue().declinationProperty());
    }

    public void setItems(List<StellarObjectModel> models) {
        stellarObjectsTable.setItems(FXCollections.observableArrayList(models));
        distanceColumn.setSortType(TableColumn.SortType.ASCENDING);
        stellarObjectsTable.getSortOrder().add(distanceColumn);
        stellarObjectsTable.getSelectionModel().selectFirst();
    }
}
