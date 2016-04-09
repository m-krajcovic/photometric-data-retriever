package cz.muni.physics.plugin;

import cz.muni.physics.model.Plugin;

import java.util.Set;

/**
 * Created by Michal on 21/03/16.
 */
public interface PluginLoader {
    Set<Plugin> getAvailablePlugins() throws PluginManagerException;
}
