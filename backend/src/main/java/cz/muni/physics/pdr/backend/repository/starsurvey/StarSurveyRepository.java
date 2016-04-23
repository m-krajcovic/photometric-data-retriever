package cz.muni.physics.pdr.backend.repository.starsurvey;

import cz.muni.physics.pdr.backend.entity.StarSurvey;
import cz.muni.physics.pdr.backend.repository.GenericRepository;

import java.util.Map;
import java.util.regex.Pattern;

/**
 * @author Michal Krajčovič
 * @version 1.0
 * @since 14/04/16
 */
public interface StarSurveyRepository extends GenericRepository<StarSurvey, String> {
    Map<String, Pattern> getAllPatterns();

    void insertPattern(String key, Pattern pattern);

    void removePattern(String key);

    Map<String, String> getAllValueParameters();

    void insertValueParameter(String key, String value);

    void removeValueParameter(String key);
}
