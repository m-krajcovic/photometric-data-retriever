package cz.muni.physics.pdr.app.controller;

import cz.muni.physics.pdr.backend.entity.CelestialCoordinates;
import cz.muni.physics.pdr.backend.utils.NumberUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.function.Consumer;

/**
 * @author Michal Krajčovič
 * @version 1.0
 * @since 20/04/16
 */
class SearchQueryParser {
    private Consumer<CelestialCoordinates> onCoordinates;
    private Consumer<String> onName;
    private Consumer<String> onError;

    SearchQueryParser(Consumer<CelestialCoordinates> onCoordinates, Consumer<String> onName, Consumer<String> onError) {
        if (onCoordinates == null) {
            throw new IllegalArgumentException("onCoordinates cannot be null.");
        }
        if (onName == null) {
            throw new IllegalArgumentException("onName cannot be null.");
        }
        if (onError == null) {
            throw new IllegalArgumentException("onError cannot be null.");
        }

        this.onCoordinates = onCoordinates;
        this.onName = onName;
        this.onError = onError;
    }

    void parseQuery(String query, String radius) {
        String searchText = query.trim();
        if (StringUtils.startsWithIgnoreCase(searchText, "name:")) {
            handleNameSearch(searchText.substring(5).trim(), radius);
            return;
        }
        if (StringUtils.startsWithIgnoreCase(searchText, "coords:")) {
            handleCoordsSearch(searchText.substring(7).trim(), radius);
            return;
        }
        String[] spaceSplit = searchText.split(" ");
        if (spaceSplit.length == 2 && NumberUtils.isParsable(spaceSplit[0]) && NumberUtils.isParsable(spaceSplit[1])) {
            double ra = Double.parseDouble(spaceSplit[0]);
            double dec = Double.parseDouble(spaceSplit[1]);
            if (ra < 0 || ra > 360 || dec < -90 || dec > 90) {
                handleNameSearch(searchText, radius);
                return;
            }
            handleCoordsSearch(searchText, radius);
        } else {
            handleNameSearch(searchText, radius);
        }
    }


    private void handleCoordsSearch(String query, String radius) {
        if (query.isEmpty()) {
            onError.accept("Query is empty. Insert coords in format '118.77167 +22.00139'");
        } else {
            String[] degrees = query.split(" ");
            if (degrees.length != 2 || !NumberUtils.isParsable(degrees[0]) || !NumberUtils.isParsable(degrees[1])) {
                onError.accept("Wrong format use degrees like '118.77167 +22.00139'");
            } else {
                double ra = Double.parseDouble(degrees[0]);
                double dec = Double.parseDouble(degrees[1]);
                double rad = 0.05;
                if (ra < 0 || ra > 360) {
                    onError.accept("Right Ascension must be from interval [0, 360]'");
                    return;
                } else if (dec < -90 || dec > 90) {
                    onError.accept("Declination must be from interval [-90, +90]'");
                    return;
                }
                if (!NumberUtils.isParsable(radius) || radius.isEmpty()) {
                    rad = 0.05;
                } else {
                    rad = Double.parseDouble(radius);
                }
                onCoordinates.accept(new CelestialCoordinates(ra, dec, rad));
            }
        }
    }

    private void handleNameSearch(String query, String radius) {
        if (query.isEmpty()) {
            onError.accept("Query is empty");
        } else {
            onName.accept(query);
        }
    }
}
