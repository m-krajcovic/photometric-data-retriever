package cz.muni.physics.controller;

import cz.muni.physics.javafx.CheckBoxCellFactory;
import cz.muni.physics.javafx.HyperlinkCellFactory;
import cz.muni.physics.plugin.Plugin;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

/**
 * @author Michal Krajčovič
 * @version 1.0
 * @since 20/03/16
 */
public class PluginOverviewController{
    @FXML
    private TableView<Plugin> pluginTableView;
    @FXML
    private TableColumn<Plugin, String> pluginNameColumn;
    @FXML
    private TableColumn<Plugin, Hyperlink> pluginURLColumn;
    @FXML
    private TableColumn<Plugin, Boolean> pluginEnabledColumn;
    @FXML
    private Button randomButton;

    public PluginOverviewController(){
    }

    @FXML
    private void buttonHandle(){
        pluginTableView.getItems().stream().filter(Plugin::getEnabled).forEach(x -> System.out.println(x.getName()));
    }

    @FXML
    private void initialize() {
        pluginURLColumn.setCellFactory(new HyperlinkCellFactory<>());
        pluginEnabledColumn.setCellFactory(new CheckBoxCellFactory<>());

        pluginNameColumn.setCellValueFactory(cellData -> cellData.getValue().getNameProperty());
        pluginURLColumn.setCellValueFactory(cellData -> cellData.getValue().getURLProperty());
        pluginEnabledColumn.setCellValueFactory(cellData -> cellData.getValue().getEnabledProperty());

        pluginTableView.getItems().add(new Plugin("Super WASP1", "http://www.url.com", "main.jar", "java -jar", "/plugins", true));
        pluginTableView.getItems().add(new Plugin("Super WASP2", "http://www.url.com", "main.jar", "java -jar", "/plugins", false));

    }
}
