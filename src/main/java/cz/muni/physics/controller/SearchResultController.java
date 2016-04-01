package cz.muni.physics.controller;

import cz.muni.physics.MainApp;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

/**
 * @author Michal Krajčovič
 * @version 1.0
 * @since 24/03/16
 */
public class SearchResultController{

    private MainApp mainApp;

    @FXML
    private TableView aliasTableView;
    @FXML
    private TableColumn<String, String> aliasTableColumn;

    private ObservableList<String> aliases = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        aliasTableView.setItems(aliases);
        aliasTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue()));
    }

    public ObservableList<String> getAliases() {
        return aliases;
    }

    public void setAliases(ObservableList<String> aliases) {
        this.aliases.clear();
        this.aliases.addAll(aliases);
    }


    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }
}
