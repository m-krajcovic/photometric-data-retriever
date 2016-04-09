package cz.muni.physics.controller;

import cz.muni.physics.MainApp;
import cz.muni.physics.java.PhotometricData;
import cz.muni.physics.model.StarSurvey;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.fxml.FXML;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @author Michal Krajčovič
 * @version 1.0
 * @since 01/04/16
 */
@Component
public class PhotometricDataOverviewController {

    private MainApp mainApp;

    @FXML
    private TableView<PhotometricData> photometricDataTableView;
    @FXML
    private TableColumn<PhotometricData, Number> julianDate;
    @FXML
    private TableColumn<PhotometricData, Number> magnitude;
    @FXML
    private TableColumn<PhotometricData, Number> error;
    @FXML
    private ScatterChart<Number, Number> chart;

    @FXML
    private void initialize() {
        julianDate.setCellValueFactory(cell -> new SimpleDoubleProperty(cell.getValue().getJulianDate()));
        magnitude.setCellValueFactory(cell -> new SimpleDoubleProperty(cell.getValue().getMagnitude()));
        error.setCellValueFactory(cell -> new SimpleDoubleProperty(cell.getValue().getError()));
    }

    public void setData(Map<StarSurvey, List<PhotometricData>> data) {
        photometricDataTableView.getItems().clear();
        chart.getData().clear();
        for (Map.Entry<StarSurvey, List<PhotometricData>> entry : data.entrySet()) {
            photometricDataTableView.getItems().addAll(entry.getValue());
            XYChart.Series<Number, Number> series = new XYChart.Series<>();
            series.setName(entry.getKey().getName());
            for (PhotometricData d : entry.getValue()) {
                XYChart.Data<Number, Number> e = new XYChart.Data<>(d.getJulianDate(), d.getMagnitude());
                series.getData().add(e);
            }
            chart.getData().add(series);
        }
    }

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }
}
