package cz.muni.physics.pdr.plugin;

import cz.muni.physics.pdr.model.Plugin;

import java.io.IOException;
import java.util.Map;

/**
 * @author Michal Krajčovič
 * @version 1.0
 * @since 21/03/16
 */
public interface PluginManager {
    Process run(Plugin plugin, Map<String, String> params) throws IOException;
}
