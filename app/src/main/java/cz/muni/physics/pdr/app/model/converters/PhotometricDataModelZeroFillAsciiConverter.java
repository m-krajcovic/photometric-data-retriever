package cz.muni.physics.pdr.app.model.converters;

import cz.muni.physics.pdr.app.model.PhotometricDataModel;

import java.util.List;

/**
 * Created by Michal on 28-Apr-16.
 */
public class PhotometricDataModelZeroFillAsciiConverter extends PhotometricDataModelConverter {
    @Override
    public String toString(PhotometricDataModel object) {
        return String.format("%s\t%s\t%s", object.getJulianDate(), object.getMagnitude(), object.getError());
    }

    @Override
    public PhotometricDataModel fromString(String string) {
        String[] split = string.split("\t");
        return new PhotometricDataModel(split[0], split[1], split[2]);
    }

    @Override
    public String toString(List<PhotometricDataModel> models) {
        int jdDecPMax = 0;
        int magDecPMax = 0;
        int errDecPMax = 0;
        for (PhotometricDataModel model : models) {
            int jdDecP = Double.toString(model.getJulianDate()).split("\\.")[1].length();
            int magDecP = Double.toString(model.getMagnitude()).split("\\.")[1].length();
            int errDecP = Double.toString(model.getError()).split("\\.")[1].length();
            if (jdDecP > jdDecPMax) jdDecPMax = jdDecP;
            if (magDecP > magDecPMax) magDecPMax = magDecP;
            if (errDecP > errDecPMax) errDecPMax = errDecP;
        }
        final int finalJdDecPMax = jdDecPMax;
        final int finalMagDecPMax = magDecPMax;
        final int finalErrDecPMax = errDecPMax;
        return models.stream().map((object) -> toString(object, finalJdDecPMax, finalMagDecPMax, finalErrDecPMax)).reduce(getHeader(), (s1, s2) -> s1 + System.lineSeparator() + s2);
    }

    private String toString(PhotometricDataModel object, int jd, int mag, int err) {
        String jdString = String.format("%." + jd + "f", object.getJulianDate());
        String magString = String.format("%." + mag + "f", object.getMagnitude());
        String errString = String.format("%." + err + "f", object.getError());

        return String.format("%s\t%s\t%s", jdString, magString, errString);
    }

    @Override
    public String getHeader() {
        return "# Julian Date\tMagnitude\tError";
    }

    @Override
    public String getExtension() {
        return ".txt";
    }
}
