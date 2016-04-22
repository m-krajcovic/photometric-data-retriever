package cz.muni.physics.pdr.backend.manager;

import cz.muni.physics.pdr.backend.entity.Plugin;
import cz.muni.physics.pdr.backend.entity.StarSurvey;
import cz.muni.physics.pdr.backend.repository.plugin.PluginRepository;
import cz.muni.physics.pdr.backend.repository.starsurvey.StarSurveyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.function.Predicate;

/**
 * @author Michal Krajčovič
 * @version 1.0
 * @since 14/04/16
 */
@Component
public class StarSurveyManagerImpl implements StarSurveyManager {

    private PluginRepository pluginRepository;
    private StarSurveyRepository starSurveyRepository;

    @Autowired
    public StarSurveyManagerImpl(PluginRepository pluginRepository,
                                 StarSurveyRepository starSurveyRepository){
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
}
