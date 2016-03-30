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
public class NsvsPlugin implements Plugin {
    @Override
    public PluginDescription getPluginDescription() {
        return new PluginDescription(
                "NSVS",
                "http://skydot.lanl.gov/nsvs/nsvs.php",
                ""
        );
    }

    @Override
    public List<PhotometricData> getDataByName(String name) {
        List<PhotometricData> result = new ArrayList<>();
        result.add(new PhotometricData("2", "5.3", "1.02"));
        return result;
    }

    @Override
    public List<PhotometricData> getDataByAlias(String alias) {
        return null;
    }

    @Override
    public List<PhotometricData> getDataByCoords() {
        return null;
    }
}
