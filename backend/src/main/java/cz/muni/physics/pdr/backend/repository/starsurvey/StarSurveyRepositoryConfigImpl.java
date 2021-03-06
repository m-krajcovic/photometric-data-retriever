package cz.muni.physics.pdr.backend.repository.starsurvey;

import cz.muni.physics.pdr.backend.entity.Configuration;
import cz.muni.physics.pdr.backend.entity.StarSurvey;
import cz.muni.physics.pdr.backend.repository.config.ConfigurationHolder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author Michal Krajčovič
 * @version 1.0
 * @since 23/04/16
 */
public class StarSurveyRepositoryConfigImpl implements StarSurveyRepository {
    private static final Logger logger = LogManager.getLogger(StarSurveyRepositoryConfigImpl.class);

    private Map<String, StarSurvey> starSurveysCache = new HashMap<>();
    private Map<String, Pattern> patterns = new HashMap<>();
    private ConfigurationHolder configHolder;

    public StarSurveyRepositoryConfigImpl(ConfigurationHolder configHolder) {
        this.configHolder = configHolder;
        reloadConfig(configHolder.get());
        configHolder.addOnConfigurationChange(this::reloadConfig);
    }

    @Override
    public void insert(StarSurvey entity) {
        logger.debug("Inserting key {}, value {}", entity.getName(), entity.toString());
        starSurveysCache.put(entity.getName(), new StarSurvey(entity));
        saveConfig();
    }

    @Override
    public void delete(StarSurvey entity) {
        logger.debug("Removing key {}", entity.getName());
        if (starSurveysCache.containsKey(entity.getName())) {
            logger.debug("Key {} found. Removing object.", entity.getName());
            starSurveysCache.remove(entity.getName());
            saveConfig();
        }
    }

    @Override
    public Collection<StarSurvey> getAll() {
        configHolder.get();
        List<StarSurvey> newList = new ArrayList<>(starSurveysCache.values().size());
        starSurveysCache.values().forEach(starSurvey -> newList.add(new StarSurvey(starSurvey)));
        return newList;
    }

    @Override
    public StarSurvey getById(String s) {
        configHolder.get();
        return new StarSurvey(starSurveysCache.get(s));
    }

    @Override
    public StarSurvey searchFor(Predicate<StarSurvey> predicate) {
        configHolder.get();
        Optional<StarSurvey> optional = starSurveysCache.values().stream().filter(predicate).findFirst();
        return optional.map(StarSurvey::new).orElse(null);
    }

    @Override
    public Collection<StarSurvey> searchForAll(Predicate<StarSurvey> predicate) {
        configHolder.get();
        List<StarSurvey> result = starSurveysCache.values().stream().filter(predicate).collect(Collectors.toList());
        List<StarSurvey> newList = new ArrayList<>(result.size());
        result.forEach(starSurvey -> newList.add(new StarSurvey(starSurvey)));
        return newList;
    }

    private void reloadConfig(Configuration configuration) {
        logger.debug("Reloading configuration file.");
        List<StarSurvey> starSurveys = configuration.getStarSurveys();
        starSurveysCache = new HashMap<>(starSurveys.size());
        for (StarSurvey starSurvey : starSurveys) {
            starSurveysCache.put(starSurvey.getName(), starSurvey);
        }
        patterns = new HashMap<>(configuration.getPatterns());
    }

    private void saveConfig() {
        Configuration configuration = configHolder.get();
        configuration.setStarSurveys(new ArrayList<>(starSurveysCache.values()));
        configuration.setPatterns(patterns);
        configHolder.persist(configuration);
    }

    @Override
    public Map<String, Pattern> getAllPatterns() {
        configHolder.get();
        return new HashMap<>(patterns);
    }

    @Override
    public void insertPattern(String key, Pattern pattern) {
        logger.debug("Inserting key {}, value {}", key, pattern.pattern());
        patterns.put(key, pattern);
        saveConfig();
    }

    @Override
    public void removePattern(String key) {
        logger.debug("Starting removal of key {}", key);
        if (patterns.containsKey(key)) {
            logger.debug("Key {} found. Removing object.", key);
            patterns.remove(key);
            saveConfig();
        }
    }
}
