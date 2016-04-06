package cz.muni.physics.plugin;

import cz.muni.physics.model.Plugin;

import java.util.Set;

/**
 * @author Michal Krajčovič
 * @version 1.0
 * @since 21/03/16
 */
public interface PluginManager {
    Set<Plugin> getAvailablePlugins() throws PluginManagerException;
}
