package cz.muni.physics.pdr.app.controller;

import cz.muni.physics.pdr.app.model.PhotometricDataModel;
import cz.muni.physics.pdr.app.utils.FXMLUtils;
import cz.muni.physics.pdr.backend.entity.StarSurvey;
import cz.muni.physics.pdr.backend.entity.StellarObject;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.CacheHint;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
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
    private MenuBar menuBar;
    @FXML
    private MenuItem saveAllMenuItem;
    @FXML
    private MenuItem closeMenuItem;
    @FXML
    private Menu saveMenu;
    @FXML
    private TableView<PhotometricDataModel> photometricDataTableView;
    @FXML
    private TableColumn<PhotometricDataModel, Number> julianDate;
    @FXML
    private TableColumn<PhotometricDataModel, Number> magnitude;
    @FXML
    private TableColumn<PhotometricDataModel, Number> error;
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

        julianDate.setCellValueFactory(cell -> cell.getValue().julianDateProperty());
        magnitude.setCellValueFactory(cell -> cell.getValue().magnitudeProperty());
        error.setCellValueFactory(cell -> cell.getValue().errorProperty());

        chart.setAnimated(false);
        chart.setCache(true);
        chart.setCacheHint(CacheHint.SPEED);
    }

    @FXML
    private void handleSaveAllMenuItem() { // todo must be done async
        String coords = stellarObject.getRightAscension() + " " + stellarObject.getDeclination();
        File zip = FXMLUtils.showSaveFileChooser("Choose your destiny",
                System.getProperty("user.home"),
                "pdr-export " + coords + ".zip",
                photometricDataTableView.getScene().getWindow(),
                new FileChooser.ExtensionFilter("Zip file(*.zip)", "*.zip"));
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
    }

    private String toCsv(List<PhotometricDataModel> models) {
        return models.stream().map(PhotometricDataModel::toCsv).reduce("Julian date,Magnitude,Magnitude error", (s1, s2) -> s1 + "\n" + s2);
    }

    @FXML
    private void handleCloseMenuItem() {

    }

    public void setData(Map<StarSurvey, List<PhotometricDataModel>> data) {
        this.data = data;
        photometricDataTableView.getItems().clear();
        chart.getData().clear();
        for (Map.Entry<StarSurvey, List<PhotometricDataModel>> entry : data.entrySet()) {
            photometricDataTableView.getItems().addAll(entry.getValue());

            MenuItem menuItem = new MenuItem(entry.getKey().getName());
            menuItem.setOnAction(event -> { // todo async
                String coords = stellarObject.getRightAscension() + " " + stellarObject.getDeclination();
                File csv = FXMLUtils.showSaveFileChooser("Choose your destiny",
                        System.getProperty("user.home"),
                        entry.getKey().getName() + " " + coords + ".csv",
                        photometricDataTableView.getScene().getWindow(),
                        new FileChooser.ExtensionFilter("Csv file(*.csv)", "*.csv"));
                if (csv != null) {
                    try (FileWriter writer = new FileWriter(csv)) {
                        writer.write(toCsv(entry.getValue()));
                    } catch (IOException e) {
                        e.printStackTrace(); // todo handle
                    }
                }
            });
            saveMenu.getItems().add(menuItem);
        }

        if (stellarObject.getEpoch() != null && stellarObject.getPeriod() != null) { //todo async this
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
        }
    }

    public StellarObject getStellarObject() {
        return stellarObject;
    }

    public void setStellarObject(StellarObject stellarObject) {
        this.stellarObject = stellarObject;
    }


}
