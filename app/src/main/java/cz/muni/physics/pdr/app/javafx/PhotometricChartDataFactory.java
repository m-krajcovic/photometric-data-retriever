package cz.muni.physics.pdr.app.javafx;

import cz.muni.physics.pdr.app.model.PhotometricDataModel;
import cz.muni.physics.pdr.app.model.StellarObjectModel;
import javafx.scene.chart.XYChart;

import java.util.List;
import java.util.ResourceBundle;

/**
 * @author Michal Krajčovič
 * @version 1.0
 * @since 24/04/16
 */
public abstract class PhotometricChartDataFactory {

    public static PhotometricChartDataFactory getInstance(StellarObjectModel stellarObject) {
        if (stellarObject.getPeriod() != 0 && stellarObject.getEpoch() != 0) {
            return new PeriodPhotometricChartData(stellarObject);
        } else {
            return new JulianDatePhotometricChartData();
        }
    }

    public XYChart.Series<Number, Number> getSeries(String name, List<PhotometricDataModel> models) {
        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        series.setName(name);
        int size = models.size();
        int increment = size > 10000 ? 10 : 1;
        for (int i = 0; i < size; i += increment) {
            PhotometricDataModel d = models.get(i);
            series.getData().add(getData(d));
        }
        return series;
    }

    public abstract XYChart.Data<Number, Number> getData(PhotometricDataModel model);

    public abstract void setUpChart(XYChart<Number, Number> chart, ResourceBundle resources);

}
