package cz.muni.physics;

import cz.muni.physics.java.PhotometricData;
import cz.muni.physics.java.Plugin;
import cz.muni.physics.java.PluginDescription;

import java.util.ArrayList;
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
                ""
        );
    }

    public List<PhotometricData> getDataByName(String name) {
        List<PhotometricData> result = new ArrayList<>();
        result.add(new PhotometricData("1", "2.3", "0.02"));
        return result;
    }
}
