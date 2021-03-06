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
        return getSeries(name, models, false);
    }

    public XYChart.Series<Number, Number> getSeries(String name, List<PhotometricDataModel> models, boolean forceAll) {
        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        series.setName(name);
        int size = models.size();
        int increment = !forceAll && size > 1000 ? (size > 2000 ? binlog(size) : 5) : 1;
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

    public static int binlog( int bits ) // returns 0 for bits=0
    {
        int log = 0;
        if( ( bits & 0xffff0000 ) != 0 ) { bits >>>= 16; log = 16; }
        if( bits >= 256 ) { bits >>>= 8; log += 8; }
        if( bits >= 16  ) { bits >>>= 4; log += 4; }
        if( bits >= 4   ) { bits >>>= 2; log += 2; }
        return log + ( bits >>> 1 );
    }

    public abstract XYChart.Data<Number, Number> getData(PhotometricDataModel model);

    public abstract void setUpChart(XYChart<Number, Number> chart, ResourceBundle resources);

}
