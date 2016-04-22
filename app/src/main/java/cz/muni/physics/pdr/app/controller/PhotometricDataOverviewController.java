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
import javafx.stage.FileChooser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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

        julianDate.setCellValueFactory(cell -> cell.getValue().julianDateProperty());
        magnitude.setCellValueFactory(cell -> cell.getValue().magnitudeProperty());
        error.setCellValueFactory(cell -> cell.getValue().errorProperty());

        chart.setAnimated(false);
        chart.setCache(true);
        chart.setCacheHint(CacheHint.SPEED);
    }

    @FXML
    private void handleSaveAllMenuItem() {
        File zip = FXMLUtils.showSaveFileChooser("Choose your destiny",
                System.getProperty("user.home"),
                "pdr-export-" + stellarObject.getRightAscension() + " " + stellarObject.getDeclination() + ".zip",
                photometricDataTableView.getScene().getWindow(),
                new FileChooser.ExtensionFilter("zip file", "*.zip"));
        try {
            try (ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zip))) {
                for (Map.Entry<StarSurvey, List<PhotometricDataModel>> entry : data.entrySet()) {
                    ZipEntry e = new ZipEntry(MessageFormat.format("{0} {1} {2}.csv", entry.getKey().getName(), stellarObject.getRightAscension(), stellarObject.getDeclination()));
                    out.putNextEntry(e);
                    Optional<String> os = entry.getValue().stream().map(PhotometricDataModel::toCsv).reduce((s1, s2) -> s1 + "\n" + s2);
                    if (os.isPresent()) {
                        byte[] byteArray = os.get().getBytes();
                        out.write(byteArray, 0, byteArray.length);
                    }
                    out.closeEntry();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setData(Map<StarSurvey, List<PhotometricDataModel>> data) {
        this.data = data;
        photometricDataTableView.getItems().clear();
        chart.getData().clear();
        for (Map.Entry<StarSurvey, List<PhotometricDataModel>> entry : data.entrySet()) {
            photometricDataTableView.getItems().addAll(entry.getValue());

            MenuItem menuItem = new MenuItem(entry.getKey().getName());
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
