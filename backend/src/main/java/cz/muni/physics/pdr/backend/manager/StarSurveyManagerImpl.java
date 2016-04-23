package cz.muni.physics.pdr.backend.manager;

import cz.muni.physics.pdr.backend.entity.Plugin;
import cz.muni.physics.pdr.backend.entity.StarSurvey;
import cz.muni.physics.pdr.backend.repository.plugin.PluginRepository;
import cz.muni.physics.pdr.backend.repository.starsurvey.StarSurveyRepository;

import java.util.Collection;
import java.util.Map;
import java.util.function.Predicate;
import java.util.regex.Pattern;

/**
 * @author Michal Krajčovič
 * @version 1.0
 * @since 14/04/16
 */
public class StarSurveyManagerImpl implements StarSurveyManager {

    private PluginRepository pluginRepository;
    private StarSurveyRepository starSurveyRepository;

    public StarSurveyManagerImpl(PluginRepository pluginRepository,
                                 StarSurveyRepository starSurveyRepository) {
        this.pluginRepository = pluginRepository;
        this.starSurveyRepository = starSurveyRepository;
    }

    @Override
    public void insert(StarSurvey entity) {
        starSurveyRepository.insert(entity);
    }

    @Override
    public void delete(StarSurvey entity) {
        starSurveyRepository.delete(entity);
    }

    @Override
    public Collection<StarSurvey> getAll() {
        Collection<StarSurvey> surveys = starSurveyRepository.getAll();
        for (StarSurvey survey : surveys) {
            if (survey.getPlugin().getName() != null) {
                Plugin plugin = pluginRepository.getById(survey.getPlugin().getName());
                survey.setPlugin(plugin);
            }
        }
        return surveys;
    }

    @Override
    public StarSurvey searchFor(Predicate<StarSurvey> predicate) {
        StarSurvey survey = starSurveyRepository.searchFor(predicate);
        Plugin plugin = pluginRepository.getById(survey.getName());
        survey.setPlugin(plugin);
        return survey;
    }

    @Override
    public Collection<StarSurvey> searchForAll(Predicate<StarSurvey> predicate) {
        Collection<StarSurvey> surveys = starSurveyRepository.searchForAll(predicate);
        for (StarSurvey survey : surveys) {
            survey.setPlugin(pluginRepository.getById(survey.getPlugin().getName()));
        }
        return surveys;
    }

    @Override
    public StarSurvey getById(String id) {
        StarSurvey survey = starSurveyRepository.getById(id);
        Plugin plugin = pluginRepository.getById(survey.getName());
        survey.setPlugin(plugin);
        return survey;
    }

    @Override
    public Map<String, Pattern> getAllPatterns() {
        return starSurveyRepository.getAllPatterns();
    }

    @Override
    public void insertPattern(String key, Pattern pattern) {
        starSurveyRepository.insertPattern(key, pattern);
    }

    @Override
    public void removePattern(String key) {
        starSurveyRepository.removePattern(key);
    }

    @Override
    public Map<String, String> getAllValueParameters() {
        return starSurveyRepository.getAllValueParameters();
    }

    @Override
    public void insertValueParameter(String key, String value) {
        starSurveyRepository.insertValueParameter(key, value);
    }

    @Override
    public void removeValueParameter(String key) {
        starSurveyRepository.removeValueParameter(key);
    }
}
