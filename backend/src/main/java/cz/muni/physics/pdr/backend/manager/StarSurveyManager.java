package cz.muni.physics.pdr.backend.manager;

import cz.muni.physics.pdr.backend.entity.StarSurvey;

import java.util.Map;
import java.util.regex.Pattern;

/**
 * @author Michal Krajčovič
 * @version 1.0
 * @since 14/04/16
 */
public interface StarSurveyManager extends GenericEntityManager<StarSurvey, String> {
    Map<String, Pattern> getAllPatterns();

    void insertPattern(String key, Pattern pattern);

    void removePattern(String key);
}
