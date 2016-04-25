package cz.muni.physics.pdr.backend.manager;

import cz.muni.physics.pdr.backend.entity.Plugin;
import cz.muni.physics.pdr.backend.repository.plugin.PluginRepository;

import java.util.Collection;
import java.util.function.Predicate;

/**
 * @author Michal Krajčovič
 * @version 1.0
 * @since 14/04/16
 */
public class PluginManagerImpl implements PluginManager {

    private PluginRepository pluginRepository;

    public PluginManagerImpl(PluginRepository pluginRepository){
        this.pluginRepository = pluginRepository;
    }

    @Override
    public void insert(Plugin entity) {
        throw new UnsupportedOperationException("Insert plugins manually.");
    }

    @Override
    public void delete(Plugin entity) {
        throw new UnsupportedOperationException("Delete plugins manually.");
    }

    @Override
    public Collection<Plugin> getAll()  {
        return pluginRepository.getAll();
    }

    @Override
    public Plugin searchFor(Predicate<Plugin> predicate)  {
        return pluginRepository.searchFor(predicate);
    }

    @Override
    public Collection<Plugin> searchForAll(Predicate<Plugin> predicate)  {
        return pluginRepository.searchForAll(predicate);
    }

    @Override
    public Plugin getById(String s)  {
        return pluginRepository.getById(s);
    }
}
