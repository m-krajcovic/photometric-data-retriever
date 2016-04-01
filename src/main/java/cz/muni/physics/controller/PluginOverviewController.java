package cz.muni.physics.controller;

import cz.muni.physics.MainApp;
import cz.muni.physics.javafx.CheckBoxCellFactory;
import cz.muni.physics.javafx.HyperlinkCellFactory;
import cz.muni.physics.model.Plugin;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.MessageFormat;

/**
 * @author Michal Krajčovič
 * @version 1.0
 * @since 20/03/16
 */
public class PluginOverviewController {

    private MainApp mainApp;

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

    public PluginOverviewController() {
    }

    @FXML
    private void buttonHandle() {
        pluginTableView.getItems().stream().forEach(x -> System.out.println(x.getName()));
    }

    @FXML
    private void initialize() {
        pluginURLColumn.setCellFactory(new HyperlinkCellFactory<>());
        pluginEnabledColumn.setCellFactory(new CheckBoxCellFactory<>());

        pluginNameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        pluginURLColumn.setCellValueFactory(cellData -> cellData.getValue().URLProperty());

        pluginTableView.getItems().add(new Plugin("CRTS", "http://www.url.com", "CrtsPlugin.jar", "java -jar {0}", "plugins/"));
        pluginTableView.getItems().add(new Plugin("NSVS", "http://www.url.com", "NsvsPlugin.jar", "java -cp {0} cz.muni.physics.NsvsPlugin", "plugins/"));

        try {
            for (Plugin p : pluginTableView.getItems()) {
                String command = MessageFormat.format(p.getCommand(), p.getPath() + p.getMainFile());
                System.out.println(command);
                InputStream is = Runtime.getRuntime().exec(command).getInputStream();
                InputStreamReader isr = new InputStreamReader(is);
                BufferedReader buff = new BufferedReader(isr);

                String line;
                while ((line = buff.readLine()) != null)
                    System.out.println(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }
}
