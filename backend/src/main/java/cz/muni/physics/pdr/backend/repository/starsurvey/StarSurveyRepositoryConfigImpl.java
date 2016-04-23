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
import java.util.stream.Collectors;

/**
 * @author Michal Krajčovič
 * @version 1.0
 * @since 23/04/16
 */
public class StarSurveyRepositoryConfigImpl implements StarSurveyRepository {
    private final static Logger logger = LogManager.getLogger(StarSurveyRepositoryConfigImpl.class);
    
    private Map<String, StarSurvey> starSurveysCache = new HashMap<>();
    private ConfigurationHolder configHolder;

    public StarSurveyRepositoryConfigImpl(ConfigurationHolder configHolder) {
        this.configHolder = configHolder;
        loadSurveys(configHolder.get());
        configHolder.addOnConfigurationChange(this::loadSurveys);
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

    private void loadSurveys(Configuration configuration) {
        for (StarSurvey starSurvey : configuration.getStarSurveys()) {
            starSurveysCache.put(starSurvey.getName(), starSurvey);
        }
    }

    private void saveConfig() {
        Configuration configuration = configHolder.get();
        configuration.setStarSurveys(new ArrayList<>(starSurveysCache.values()));
        configHolder.persist(configuration);
    }
}
