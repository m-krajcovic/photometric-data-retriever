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
    private final static Logger logger = LogManager.getLogger(StarSurveyRepositoryConfigImpl.class);

    private Map<String, StarSurvey> starSurveysCache = new HashMap<>();
    private Map<String, Pattern> patterns = new HashMap<>();
    private Map<String, String> valueParameters = new HashMap<>();
    private ConfigurationHolder configHolder;

    public StarSurveyRepositoryConfigImpl(ConfigurationHolder configHolder) {
        this.configHolder = configHolder;
        reloadConfig(configHolder.get());
        configHolder.addOnConfigurationChange(this::reloadConfig);
    }

    @Override
    public void insert(StarSurvey entity) {
        starSurveysCache.put(entity.getName(), new StarSurvey(entity));
        saveConfig();
    }

    @Override
    public void delete(StarSurvey entity) {
        if (starSurveysCache.containsKey(entity.getName())) {
            starSurveysCache.remove(entity.getName());
            saveConfig();
        }
    }

    @Override
    public Collection<StarSurvey> getAll() {
        List<StarSurvey> newList = new ArrayList<>(starSurveysCache.values().size());
        starSurveysCache.values().forEach(starSurvey -> newList.add(new StarSurvey(starSurvey)));
        return newList;
    }

    @Override
    public StarSurvey getById(String s) {
        return new StarSurvey(starSurveysCache.get(s));
    }

    @Override
    public StarSurvey searchFor(Predicate<StarSurvey> predicate) {
        Optional<StarSurvey> optional = starSurveysCache.values().stream().filter(predicate).findFirst();
        if (optional.isPresent()) {
            return new StarSurvey(optional.get());
        }
        return null;
    }

    @Override
    public Collection<StarSurvey> searchForAll(Predicate<StarSurvey> predicate) {
        List<StarSurvey> result = starSurveysCache.values().stream().filter(predicate).collect(Collectors.toList());
        List<StarSurvey> newList = new ArrayList<>(result.size());
        result.forEach(starSurvey -> newList.add(new StarSurvey(starSurvey)));
        return newList;
    }

    private void reloadConfig(Configuration configuration) {
        for (StarSurvey starSurvey : configuration.getStarSurveys()) {
            starSurveysCache.clear();
            starSurveysCache.put(starSurvey.getName(), starSurvey);
        }
        patterns = new HashMap<>(configuration.getPatterns());
        valueParameters = new HashMap<>(configuration.getValueParameters());
    }

    private void saveConfig() {
        Configuration configuration = configHolder.get();
        configuration.setStarSurveys(new ArrayList<>(starSurveysCache.values()));
        configHolder.persist(configuration);
    }

    @Override
    public Map<String, Pattern> getAllPatterns() {
        return new HashMap<>(patterns);
    }

    @Override
    public void insertPattern(String key, Pattern pattern) {
        patterns.put(key, pattern);
        saveConfig();
    }

    @Override
    public void removePattern(String key) {
        if (patterns.containsKey(key)) {
            patterns.remove(key);
            saveConfig();
        }
    }

    @Override
    public Map<String, String> getAllValueParameters() {
        return new HashMap<>(valueParameters);
    }

    @Override
    public void insertValueParameter(String key, String value) {
        valueParameters.put(key, value);
        saveConfig();
    }

    @Override
    public void removeValueParameter(String key) {
        if (valueParameters.containsKey(key)) {
            valueParameters.remove(key);
            saveConfig();
        }
    }
}
