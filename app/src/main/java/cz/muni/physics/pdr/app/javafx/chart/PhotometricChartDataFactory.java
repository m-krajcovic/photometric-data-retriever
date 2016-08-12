package cz.muni.physics.pdr.app.javafx.chart;

import cz.muni.physics.pdr.app.model.PhotometricDataModel;
import cz.muni.physics.pdr.app.model.StellarObjectModel;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.util.StringConverter;

import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Factory class to get the right chart for given StellarObjectModel
 * @author Michal Krajčovič
 * @version 1.0
 * @since 24/04/16
 */
public abstract class PhotometricChartDataFactory {

    public static PhotometricChartDataFactory getInstance(StellarObjectModel stellarObject) {
        if (stellarObject.getPeriod() != 0) {
            return new PeriodPhotometricChartData(stellarObject);
        } else {
            return new JulianDatePhotometricChartData();
        }
    }

    public XYChart.Series<Number, Number> getSeries(String name, List<PhotometricDataModel> models) {
        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        series.setName(name);
        int size = models.size();
        int increment = size > 1000 ? (size > 2000 ? 10 : 5) : 1;
        for (int i = 0; i < size; i += increment) {
            PhotometricDataModel d = models.get(i);
            if (d.getMagnitude() != 0)
                series.getData().add(getData(d));
        }
        return series;
    }

    protected StringConverter<Number> invertedNegative(NumberAxis yAxis) {
        return new NumberAxis.DefaultFormatter(yAxis) {
            @Override
            public String toString(Number value) {
                return String.format(Locale.ENGLISH, "%7.1f", -value.doubleValue());
            }
        };
    }

    public abstract XYChart.Data<Number, Number> getData(PhotometricDataModel model);

    public abstract void setUpChart(XYChart<Number, Number> chart, ResourceBundle resources);

}
