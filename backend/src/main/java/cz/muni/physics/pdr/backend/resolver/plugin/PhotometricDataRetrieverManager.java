package cz.muni.physics.pdr.backend.resolver.plugin;

import cz.muni.physics.pdr.backend.entity.PhotometricData;
import cz.muni.physics.pdr.backend.entity.StarSurvey;
import cz.muni.physics.pdr.backend.entity.StellarObject;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Class for retrieving photometric data from plugins
 * @author Michal Krajčovič
 * @version 1.0
 * @since 14/04/16
 */
public interface PhotometricDataRetrieverManager {
    /**
     * Returns map with StarSurvey and found photometric data for it
     * @param result
     * @return
     */
    Map<StarSurvey, List<PhotometricData>> runAll(StellarObject result);

    /**
     * Given consumer is called when no results are found in runAll
     * @param onNoResultsFound
     */
    void setOnNoResultsFound(Consumer<StarSurvey> onNoResultsFound);

    /**
     * Given consumer is called when some results were found in runAll
     * @param onResultsFound
     */
    void setOnResultsFound(Consumer<StarSurvey> onResultsFound);

    /**
     * Cancels all plugin processes
     */
    void cancelAll();
}
