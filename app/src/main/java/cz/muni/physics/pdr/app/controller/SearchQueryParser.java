package cz.muni.physics.pdr.app.controller;

import cz.muni.physics.pdr.app.utils.DefaultHashMap;
import cz.muni.physics.pdr.app.utils.NumberUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Michal Krajčovič
 * @version 1.0
 * @since 20/04/16
 */
class SearchQueryParser {
    private final static Logger logger = LogManager.getLogger(SearchQueryParser.class);

    private BiConsumer<String, Double> onCoordinates;
    private Consumer<String> onName;
    private Consumer<String> onError;
    private Map<Pattern, BiConsumer<String, String>> strategies;

    SearchQueryParser(BiConsumer<String, Double> onCoordinates, Consumer<String> onName, Consumer<String> onError) {
        if (onCoordinates == null) {
            throw new IllegalArgumentException("onCoordinates cannot be null.");
        }
        if (onName == null) {
            throw new IllegalArgumentException("onName cannot be null.");
        }
        if (onError == null) {
            throw new IllegalArgumentException("onError cannot be null.");
        }

        strategies = new DefaultHashMap<>(this::handleNameSearch);
        strategies.put(Pattern.compile("(\\d+\\.?\\d*)\\s([\\+\\-]?\\d+\\.?\\d*)"), this::handleDegreesCoordsSearch);
        strategies.put(Pattern.compile("(\\d{2}\\s\\d{2}\\s\\d{2}\\.?\\d*)\\s([\\+\\-]?\\d{2}\\s\\d{2}\\s\\d{2}\\.?\\d*)"), this::handleCoordsSearch);
        strategies.put(Pattern.compile("(\\d{2}:\\d{2}:\\d{2}\\.?\\d*)\\s([\\+\\-]?\\d{2}:\\d{2}:\\d{2}\\.?\\d*)"), this::handleCoordsSearch);

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
            handleDegreesCoordsSearch(searchText.substring(7).trim(), radius);
            return;
        }

        for (Pattern pattern : strategies.keySet()) {
            Matcher m = pattern.matcher(query);
            if (m.matches()) {
                logger.debug("Query '{}' matches pattern '{}'", query, pattern.pattern());
                strategies.get(pattern).accept(query, radius);
                return;
            }
        }
        logger.debug("Query '{}' doesn't match any pattern, using default", query);
        strategies.get(null).accept(query, radius);
    }


    private void handleDegreesCoordsSearch(String query, String radius) {
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
                onCoordinates.accept(query, rad);
            }
        }
    }

    private void handleCoordsSearch(String query, String radius) {
        // todo validate this
        double rad = 0.05;
        if (!radius.isEmpty() && NumberUtils.isParsable(radius)) {
            rad = Double.parseDouble(radius);
        }
        onCoordinates.accept(query, rad);
    }

    private void handleNameSearch(String query, String radius) {
        if (query.isEmpty()) {
            onError.accept("Query is empty");
        } else {
            onName.accept(query);
        }
    }
}
