package cz.muni.physics.java;

import java.util.List;

/**
 * @author Michal Krajčovič
 * @version 1.0
 * @since 25/03/16
 */
public interface Plugin {
    PluginDescription getPluginDescription();

    List<PhotometricData> getDataFromUrl(String url);
}
