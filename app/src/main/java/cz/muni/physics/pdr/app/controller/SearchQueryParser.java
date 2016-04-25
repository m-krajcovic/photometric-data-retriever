package cz.muni.physics.pdr.app.controller;

import cz.muni.physics.pdr.app.utils.NumberUtils;
import cz.muni.physics.pdr.backend.entity.CelestialCoordinates;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.function.Consumer;

/**
 * @author Michal Krajčovič
 * @version 1.0
 * @since 20/04/16
 */
class SearchQueryParser {
    private final static Logger logger = LogManager.getLogger(SearchQueryParser.class);

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
        logger.debug("Parsing query {}", searchText);
        if (StringUtils.startsWithIgnoreCase(searchText, "name:")) {
            logger.debug("Query has name: prefix, handling name search");
            handleNameSearch(searchText.substring(5).trim(), radius);
            return;
        }
        if (StringUtils.startsWithIgnoreCase(searchText, "coords:")) {
            logger.debug("Query has coords: prefix, handling coords search");
            handleCoordsSearch(searchText.substring(7).trim(), radius);
            return;
        }
        String[] spaceSplit = searchText.split(" ");
        if (spaceSplit.length == 2 && NumberUtils.isParsable(spaceSplit[0]) && NumberUtils.isParsable(spaceSplit[1])) {
            double ra = Double.parseDouble(spaceSplit[0]);
            double dec = Double.parseDouble(spaceSplit[1]);
            if (ra < 0 || ra > 360 || dec < -90 || dec > 90) {
                logger.debug("Query does not have valid coords ra={}, dec={}, handling name search", ra, dec);
                handleNameSearch(searchText, radius);
                return;
            }
            logger.debug("Query has valid coords ra={}, dec={}, handling coords search", ra, dec);
            handleCoordsSearch(searchText, radius);
        } else {
            logger.debug("No number-only query, handling name search");
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
