package cz.muni.physics.pdr.app.model.converters;

import cz.muni.physics.pdr.app.model.PhotometricDataModel;

/**
 * Created by Michal on 28-Apr-16.
 */
public class PhotometricDataModelFixedLengthAsciiConverter extends PhotometricDataModelConverter {

    private static final int JD_LENGTH = -30;
    private static final int MAG_LENGTH = -15;
    private static final int ERR_LENGTH = -15;

    @Override
    public String toString(PhotometricDataModel object) {
        return String.format("%1$" + JD_LENGTH + "s\t%2$" + MAG_LENGTH + "s\t%3$" + ERR_LENGTH + "s", object.getJulianDate(), object.getMagnitude(), object.getError());
    }

    @Override
    public PhotometricDataModel fromString(String string) {
        String[] split = string.split("\t");
        return new PhotometricDataModel(split[0], split[1], split[2]);
    }

    @Override
    public String getHeader() {
        return String.format("%1$" + JD_LENGTH + "s\t%2$" + MAG_LENGTH + "s\t%3$" + ERR_LENGTH + "s",
                "Julian Date", "Magnitude", "Error");
    }

    @Override
    public String getExtension() {
        return ".txt";
    }
}
