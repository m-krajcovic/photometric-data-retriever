package cz.muni.physics.pdr.app.controller;

import cz.muni.physics.pdr.app.model.PhotometricDataModel;
import cz.muni.physics.pdr.backend.entity.StarSurvey;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.CacheHint;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

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
    private TableView<PhotometricDataModel> photometricDataTableView;
    @FXML
    private TableColumn<PhotometricDataModel, Number> julianDate;
    @FXML
    private TableColumn<PhotometricDataModel, Number> magnitude;
    @FXML
    private TableColumn<PhotometricDataModel, Number> error;
    @FXML
    private ScatterChart<Number, Number> chart;

    @FXML
    private void initialize() {
        julianDate.setCellValueFactory(cell -> cell.getValue().julianDateProperty());
        magnitude.setCellValueFactory(cell -> cell.getValue().magnitudeProperty());
        error.setCellValueFactory(cell -> cell.getValue().errorProperty());

        chart.setAnimated(false);
        chart.setCache(true);
        chart.setCacheHint(CacheHint.SPEED);
    }

    public void setData(Map<StarSurvey, List<PhotometricDataModel>> data) {
        photometricDataTableView.getItems().clear();
        chart.getData().clear();
        for (Map.Entry<StarSurvey, List<PhotometricDataModel>> entry : data.entrySet()) {
            photometricDataTableView.getItems().addAll(entry.getValue());
        }

        Platform.runLater(() -> {
            logger.debug("Start");
            ObservableList<XYChart.Series<Number, Number>> obsList = FXCollections.observableArrayList();
            for (Map.Entry<StarSurvey, List<PhotometricDataModel>> entry : data.entrySet()) {
                XYChart.Series<Number, Number> series = new XYChart.Series<>();
                series.setName(entry.getKey().getName());
                for (PhotometricDataModel d : entry.getValue()) {
                    XYChart.Data<Number, Number> e = new XYChart.Data<>(d.getJulianDate(), d.getMagnitude());
                    series.getData().add(e);
                }
                logger.debug("Something ended");
                obsList.add(series);
            }
            logger.debug("End");
        });
    }
}
