package cz.muni.physics.pdr.app.javafx.chart;

import cz.muni.physics.pdr.app.model.PhotometricDataModel;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;

import java.util.ResourceBundle;

/**
 * @author Michal Krajčovič
 * @version 1.0
 * @since 24/04/16
 */
class JulianDatePhotometricChartData extends PhotometricChartDataFactory {
    @Override
    public XYChart.Data<Number, Number> getData(PhotometricDataModel model) {
        return new XYChart.Data<>(model.getJulianDate(), -model.getMagnitude());
    }

    @Override
    public void setUpChart(XYChart<Number, Number> chart, ResourceBundle resources) {
        NumberAxis xAxis = (NumberAxis) chart.getXAxis();
        NumberAxis yAxis = (NumberAxis) chart.getYAxis();
        xAxis.setLabel(resources.getString("photometricdata.jd"));
        xAxis.setAutoRanging(true);
        xAxis.setTickUnit(1000);
        yAxis.setLabel(resources.getString("photometricdata.magnitude"));
        yAxis.setAutoRanging(true);
        yAxis.setTickLabelFormatter(invertedNegative(yAxis));
    }
}
