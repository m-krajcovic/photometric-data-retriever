package cz.muni.physics.pdr.backend.repository.plugin;

import cz.muni.physics.pdr.backend.entity.Plugin;
import cz.muni.physics.pdr.backend.repository.GenericRepository;

/**
 * @author Michal Krajčovič
 * @version 1.0
 * @since 14/04/16
 */
public interface PluginRepository extends GenericRepository<Plugin, String> {
    default Plugin getById(String s)  {
        return searchFor(plugin -> plugin.getName().equalsIgnoreCase(s));
    }
}
