package cz.muni.physics.pdr.backend.repository.plugin;

import cz.muni.physics.pdr.backend.entity.Plugin;
import cz.muni.physics.pdr.backend.exception.PluginReaderException;

/**
 * Implementation fo this class is used to read plugin data from plugin config file
 * @author Michal Krajčovič
 * @version 1.0
 * @since 13/04/16
 */
public interface PluginReader {
    /**
     * Retrieves plugin information
     * @return Plugin object
     * @throws PluginReaderException if plugin config file is not available/or some information is missing
     */
    Plugin readPlugin() throws PluginReaderException;
}
