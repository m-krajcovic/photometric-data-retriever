package cz.muni.physics.pdr.app.controller;

import cz.muni.physics.pdr.app.model.PhotometricDataModel;
import cz.muni.physics.pdr.app.utils.FXMLUtils;
import cz.muni.physics.pdr.backend.entity.StarSurvey;
import cz.muni.physics.pdr.backend.entity.StellarObject;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.CacheHint;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.stage.FileChooser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @author Michal Krajčovič
 * @version 1.0
 * @since 01/04/16
 */
@Component
@Scope("prototype")
public class PhotometricDataOverviewController {

    private final static Logger logger = LogManager.getLogger(PhotometricDataOverviewController.class);
    @FXML
    private ResourceBundle resources;
    @FXML
    private Label infoLabel;
    @FXML
    private MenuBar menuBar;
    @FXML
    private MenuItem saveAllMenuItem;
    @FXML
    private MenuItem closeMenuItem;
    @FXML
    private Menu saveMenu;
    @FXML
    private TabPane tabPane;
    @FXML
    private ScatterChart<Number, Number> chart;

    private Map<StarSurvey, List<PhotometricDataModel>> data;
    private StellarObject stellarObject;

    @FXML
    private void initialize() {
        final String os = System.getProperty("os.name");
        if (os != null && os.startsWith("Mac"))
            menuBar.useSystemMenuBarProperty().set(true);
        saveAllMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.SHORTCUT_DOWN));
        closeMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.W, KeyCombination.SHORTCUT_DOWN));

        chart.setAnimated(false);
        chart.setCache(true);
        chart.setCacheHint(CacheHint.SPEED);
    }

    @FXML
    private void handleSaveAllMenuItem() {
        String coords = stellarObject.getRightAscension() + " " + stellarObject.getDeclination();
        File zip = FXMLUtils.showSaveFileChooser("Choose your destiny",
                System.getProperty("user.home"),
                "pdr-export " + coords + ".zip",
                menuBar.getScene().getWindow(),
                new FileChooser.ExtensionFilter("Zip file(*.zip)", "*.zip"));
        Task task = new Task() {
            @Override
            protected Object call() throws Exception {
                if (zip != null) {
                    try (ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zip))) {
                        for (Map.Entry<StarSurvey, List<PhotometricDataModel>> entry : data.entrySet()) {
                            ZipEntry e = new ZipEntry(MessageFormat.format("{0} {1}.csv", entry.getKey().getName(), coords));
                            out.putNextEntry(e);
                            String csv = toCsv(entry.getValue());
                            byte[] byteArray = csv.getBytes();
                            out.write(byteArray, 0, byteArray.length);
                            out.closeEntry();
                        }
                    } catch (IOException e) {
                        e.printStackTrace(); //todo handle
                    }
                }
                return null;
            }
        };
        Dialog dialog = FXMLUtils.getProgressDialog(menuBar.getScene().getWindow(), task);
        new Thread(task).start(); // todo executor
        dialog.showAndWait();
    }

    private String toCsv(List<PhotometricDataModel> models) {
        return models.stream().map(PhotometricDataModel::toCsv).reduce("Julian date,Magnitude,Magnitude error", (s1, s2) -> s1 + "\n" + s2);
    }

    @FXML
    private void handleCloseMenuItem() {

    }

    public void setData(Map<StarSurvey, List<PhotometricDataModel>> data) {
        this.data = data;
        chart.getData().clear();
        for (Map.Entry<StarSurvey, List<PhotometricDataModel>> entry : data.entrySet()) {
            try {
                TableView<PhotometricDataModel> table = FXMLLoader.load(this.getClass().getResource("/view/PhotometricDataTable.fxml"), resources);

                TableColumn<PhotometricDataModel, Number> julianDate = new TableColumn<>(resources.getString("photometricdata.jd"));
                julianDate.setCellValueFactory(param -> param.getValue().julianDateProperty());
                TableColumn<PhotometricDataModel, Number> magnitude = new TableColumn<>(resources.getString("photometricdata.magnitude"));
                magnitude.setCellValueFactory(param -> param.getValue().magnitudeProperty());
                TableColumn<PhotometricDataModel, Number> error = new TableColumn<>(resources.getString("photometricdata.error"));
                error.setCellValueFactory(param -> param.getValue().errorProperty());

                table.getColumns().set(0, julianDate);
                table.getColumns().set(1, magnitude);
                table.getColumns().set(2, error);

                table.getItems().addAll(entry.getValue());

                Tab tab = new Tab(String.format("%s (%d)", entry.getKey().getName(), entry.getValue().size()), table);
                tab.setClosable(false);
                tabPane.getTabs().add(tab);
            } catch (IOException e) {
                throw new RuntimeException("Failed to load .fxml resource", e);
            }
            MenuItem menuItem = new MenuItem(entry.getKey().getName());
            menuItem.setOnAction(event -> {
                String coords = stellarObject.getRightAscension() + " " + stellarObject.getDeclination();
                File csv = FXMLUtils.showSaveFileChooser("Choose your destiny",
                        System.getProperty("user.home"),
                        entry.getKey().getName() + " " + coords + ".csv",
                        menuBar.getScene().getWindow(),
                        new FileChooser.ExtensionFilter("Csv file(*.csv)", "*.csv"));
                Task task = new Task() {
                    @Override
                    protected Object call() throws Exception {
                        if (csv != null) {
                            try (FileWriter writer = new FileWriter(csv)) {
                                writer.write(toCsv(entry.getValue()));
                            } catch (IOException e) {
                                e.printStackTrace(); // todo handle
                            }
                        }
                        return null;
                    }
                };
                Dialog dialog = FXMLUtils.getProgressDialog(menuBar.getScene().getWindow(), task);
                new Thread(task).start(); //  todo executor
                dialog.showAndWait();
            });
            saveMenu.getItems().add(menuItem);
        }

        if (stellarObject.getEpoch() != null && stellarObject.getPeriod() != null) { //todo async this
            Task task = new Task() {
                @Override
                protected Object call() throws Exception {
                    return null;
                }
            };
            ObservableList<XYChart.Series<Number, Number>> obsList = FXCollections.observableArrayList();
            for (Map.Entry<StarSurvey, List<PhotometricDataModel>> entry : data.entrySet()) {
                XYChart.Series<Number, Number> series = new XYChart.Series<>();
                series.setName(entry.getKey().getName());
                for (PhotometricDataModel d : entry.getValue()) {
                    XYChart.Data<Number, Number> e = new XYChart.Data<>(((d.getJulianDate() - stellarObject.getEpoch()) / stellarObject.getPeriod()) % 1, d.getMagnitude());
                    series.getData().add(e);
                }
                obsList.add(series);
            }
            chart.getData().addAll(obsList);
        } else {
            chart.setDisable(true);
            chart.setOpacity(0.3);
            infoLabel.setVisible(true);
        }
    }

    public StellarObject getStellarObject() {
        return stellarObject;
    }

    public void setStellarObject(StellarObject stellarObject) {
        this.stellarObject = stellarObject;
    }


}
