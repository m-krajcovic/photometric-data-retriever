package cz.muni.physics.crts;

import cz.muni.physics.java.PhotometricData;
import cz.muni.physics.java.Plugin;
import cz.muni.physics.java.PluginDescription;

import java.util.List;

/**
 * @author Michal Krajčovič
 * @version 1.0
 * @since 25/03/16
 */
public class CrtsPlugin implements Plugin {

    public PluginDescription getPluginDescription() {
        return new PluginDescription(
                "CRTS",
                "",
                ""
        );
    }

    @Override
    public List<PhotometricData> getDataFromUrl(String url) {
        return null;
    }
}
