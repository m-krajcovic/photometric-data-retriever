package cz.muni.physics.pdr.app.model.converters;

import cz.muni.physics.pdr.app.model.PhotometricDataModel;

/**
 * Created by Michal on 28-Apr-16.
 */
public class PhotometricDataModelCsvConverter extends PhotometricDataModelConverter {
    @Override
    public String toString(PhotometricDataModel object) {
            return object.getJulianDate() + "," + object.getMagnitude() + "," + object.getError();
    }

    @Override
    public PhotometricDataModel fromString(String string) {
        String[] split = string.split(",");
        return new PhotometricDataModel(split[0], split[1], split[2]);
    }

    @Override
    public String getHeader() {
        return "Julian date,Magnitude,Magnitude error";
    }

    @Override
    public String getExtension() {
        return ".csv";
    }
}
