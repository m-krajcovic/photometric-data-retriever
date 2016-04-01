package cz.muni.physics.controller;

import cz.muni.physics.MainApp;
import cz.muni.physics.java.PhotometricData;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.fxml.FXML;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.List;

/**
 * @author Michal Krajčovič
 * @version 1.0
 * @since 01/04/16
 */
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
    private void initialize(){
        julianDate.setCellValueFactory(cell -> new SimpleDoubleProperty(cell.getValue().getJulianDate()));
        magnitude.setCellValueFactory(cell -> new SimpleDoubleProperty(cell.getValue().getMagnitude()));
        error.setCellValueFactory(cell -> new SimpleDoubleProperty(cell.getValue().getError()));

        chart.setLegendVisible(false);


    }

    public void setData(List<PhotometricData> data){
        photometricDataTableView.getItems().clear();
        photometricDataTableView.getItems().addAll(data);

        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        for(PhotometricData d : data){
            Rectangle rect1 = new Rectangle(3, 3);
            rect1.setFill(Color.RED);
            XYChart.Data<Number, Number> e = new XYChart.Data<>(d.getJulianDate(), d.getMagnitude());
            e.setNode(rect1);
            series.getData().add(e);
        }
        chart.getData().clear();
        chart.getData().add(series);
    }

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }

}
