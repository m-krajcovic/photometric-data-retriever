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
     * Given consumer after each plugin's finish
     * @param onSearchFinish
     */
    void setOnSearchFinish(Consumer<PluginSearchFinishResult> onSearchFinish);

    /**
     * Cancels all plugin processes
     */
    void cancelAll();
}
