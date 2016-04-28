package cz.muni.physics.pdr.app.model.converters;

import cz.muni.physics.pdr.app.model.PhotometricDataModel;
import javafx.util.StringConverter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Michal on 28-Apr-16.
 */
public abstract class PhotometricDataModelConverter extends StringConverter<PhotometricDataModel> {

    private static final Map<String, PhotometricDataModelConverter> converters = new HashMap<>();

    static {
        PhotometricDataModelConverter csvConverter = new PhotometricDataModelCsvConverter();
        PhotometricDataModelConverter asciiConverter = new PhotometricDataModelZeroFillAsciiConverter();

        converters.put("csv", csvConverter);
        converters.put("ascii", asciiConverter);
        converters.put("txt", asciiConverter);
    }

    public static PhotometricDataModelConverter get(String type) {
        if (type.indexOf('.') != -1)
            type = type.substring(type.lastIndexOf('.') + 1, type.length());
        return converters.getOrDefault(type, new PhotometricDataModelCsvConverter());
    }

    public abstract String getHeader();

    public abstract String getExtension();

    public String toString(List<PhotometricDataModel> models) {
        return models.stream().map(this::toString).reduce(getHeader(), (s1, s2) -> s1 + System.lineSeparator() + s2);
    }

}
