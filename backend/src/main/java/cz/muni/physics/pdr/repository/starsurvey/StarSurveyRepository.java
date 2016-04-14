package cz.muni.physics.pdr.repository.starsurvey;

import cz.muni.physics.pdr.entity.StarSurvey;
import cz.muni.physics.pdr.repository.GenericRepository;

/**
 * @author Michal Krajčovič
 * @version 1.0
 * @since 14/04/16
 */
public interface StarSurveyRepository extends GenericRepository<StarSurvey, String> {
    default StarSurvey getById(String s) {
        return searchFor(survey -> survey.getName().equalsIgnoreCase(s));
    }
}
