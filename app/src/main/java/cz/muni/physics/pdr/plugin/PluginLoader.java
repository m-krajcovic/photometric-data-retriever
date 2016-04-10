package cz.muni.physics.pdr.plugin;

import cz.muni.physics.pdr.model.Plugin;

import java.util.Set;

/**
 * @author Michal Krajčovič
 * @version 1.0
 * @since 21/03/16
 */
public interface PluginLoader {
    Set<Plugin> getAvailablePlugins() throws PluginManagerException;
}
