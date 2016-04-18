package cz.muni.physics.pdr.backend.plugin;

import cz.muni.physics.pdr.backend.entity.PhotometricData;
import cz.muni.physics.pdr.backend.entity.StarSurvey;
import cz.muni.physics.pdr.backend.entity.StellarObject;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * @author Michal Krajčovič
 * @version 1.0
 * @since 14/04/16
 */
public interface StarSurveyPluginStarter {
    Map<StarSurvey, List<PhotometricData>> runAll(StellarObject result);

    void setOnNoResultsFound(Consumer<StarSurvey> onNoResultsFound);

    void setOnResultsFound(Consumer<StarSurvey> onResultsFound);

    void cancelAll();
}
