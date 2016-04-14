package cz.muni.physics.pdr.backend.repository.plugin;

import cz.muni.physics.pdr.backend.entity.Plugin;
import cz.muni.physics.pdr.backend.plugin.PluginManagerException;

import java.io.File;

/**
 * @author Michal Krajčovič
 * @version 1.0
 * @since 13/04/16
 */
public interface PluginReader {
    Plugin readPlugin() throws PluginManagerException;
    void setPluginDir(File pluginDir);
}
