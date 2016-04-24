package cz.muni.physics.pdr.app.javafx;

import cz.muni.physics.pdr.app.model.PhotometricDataModel;
import cz.muni.physics.pdr.app.model.StellarObjectModel;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;

import java.util.ResourceBundle;

/**
 * @author Michal Krajčovič
 * @version 1.0
 * @since 24/04/16
 */
class PeriodPhotometricChartData extends PhotometricChartDataFactory {
    private StellarObjectModel stellarObject;

    public PeriodPhotometricChartData(StellarObjectModel stellarObject) {
        this.stellarObject = stellarObject;
    }

    @Override
    public XYChart.Data<Number, Number> getData(PhotometricDataModel model) {
        double period = ((((model.getJulianDate() - stellarObject.getEpoch()) / stellarObject.getPeriod()) % 1) + 1) % 1;
        return new XYChart.Data<>(period, model.getMagnitude());
    }

    @Override
    public void setUpChart(XYChart<Number, Number> chart, ResourceBundle resources) {
        NumberAxis xAxis = (NumberAxis) chart.getXAxis();
        NumberAxis yAxis = (NumberAxis) chart.getYAxis();
        xAxis.setLabel(resources.getString("photometricdata.period"));
        xAxis.setTickUnit(0.25);
        xAxis.setLowerBound(0);
        xAxis.setUpperBound(1);
        yAxis.setLabel(resources.getString("photometricdata.magnitude"));
        yAxis.setAutoRanging(true);
    }
}
