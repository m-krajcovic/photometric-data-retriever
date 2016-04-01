package cz.muni.physics.plugin;

import cz.muni.physics.model.Plugin;

import java.util.List;

/**
 * @author Michal Krajčovič
 * @version 1.0
 * @since 21/03/16
 */
public interface PluginManager {
    List<Plugin> getAvailablePlugins() throws PluginManagerException;
}
