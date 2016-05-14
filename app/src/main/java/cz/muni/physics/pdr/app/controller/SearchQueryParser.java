package cz.muni.physics.pdr.app.controller;

import cz.muni.physics.pdr.app.model.SearchModel;
import cz.muni.physics.pdr.app.utils.DefaultHashMap;
import cz.muni.physics.pdr.backend.utils.NumberUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;
import java.util.ResourceBundle;
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

    private Consumer<SearchModel> onCoordinates;
    private Consumer<SearchModel> onName;
    private Consumer<String> onError;
    private Map<Pattern, Consumer<SearchModel>> strategies;
    private ResourceBundle resources;

    SearchQueryParser(Consumer<SearchModel> onCoordinates, Consumer<SearchModel> onName, Consumer<String> onError, ResourceBundle resources) {
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
        strategies.put(Pattern.compile("(\\d{2}[\\s:]\\d{2}[\\s:]\\d{2}\\.?\\d*),?\\s([\\+\\-]?\\d{2}[\\s:]\\d{2}[\\s:]\\d{2}\\.?\\d*)"), this::handleCoordsSearch);

        this.resources = resources;
        this.onCoordinates = onCoordinates;
        this.onName = onName;
        this.onError = onError;
    }

    void parseQuery(SearchModel model) {
        String searchText = model.getQuery().trim();
        logger.debug("Parsing query {}", searchText);
        if (StringUtils.startsWithIgnoreCase(searchText, "name:")) {
            logger.debug("Query has name: prefix, handling name search");
            model.setQuery(searchText.substring(5).trim());
            handleNameSearch(model);
            return;
        }
        if (StringUtils.startsWithIgnoreCase(searchText, "coords:")) {
            logger.debug("Query has coords: prefix, handling coords search");
            model.setQuery(searchText.substring(7).trim());
            handleDegreesCoordsSearch(model);
            return;
        }

        for (Pattern pattern : strategies.keySet()) {
            Matcher m = pattern.matcher(model.getQuery());
            if (m.matches()) {
                logger.debug("Query '{}' matches pattern '{}'", model.getQuery(), pattern.pattern());
                strategies.get(pattern).accept(model);
                return;
            }
        }
        logger.debug("Query '{}' doesn't match any pattern, using default", model.getQuery());
        strategies.get(null).accept(model);
    }


    private void handleDegreesCoordsSearch(SearchModel model) {
        if (model.getQuery().isEmpty()) {
            onError.accept(resources.getString("empty.coords.query"));
        } else {
            String[] degrees = model.getQuery().split(" ");
            if (degrees.length != 2 || !NumberUtils.isParsable(degrees[0]) || !NumberUtils.isParsable(degrees[1])) {
                onError.accept(resources.getString("coords.query.format"));
            } else {
                double ra = Double.parseDouble(degrees[0]);
                double dec = Double.parseDouble(degrees[1]);
                if (ra < 0 || ra > 360) {
                    onError.accept(resources.getString("coords.ra.interval"));
                    return;
                } else if (dec < -90 || dec > 90) {
                    onError.accept(resources.getString("coords.dec.interval"));
                    return;
                }
                model.setQuery(ra + " " + dec);
                onCoordinates.accept(model);
            }
        }
    }

    private void handleCoordsSearch(SearchModel model) {
        // todo validate this
        onCoordinates.accept(model);
    }

    private void handleNameSearch(SearchModel model) {
        if (model.getQuery().isEmpty()) {
            onError.accept(resources.getString("query.is.empty"));
        } else {
            onName.accept(model);
        }
    }
}
