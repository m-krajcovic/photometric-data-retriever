package cz.muni.physics.pdr.backend.manager;

import cz.muni.physics.pdr.backend.entity.Plugin;
import cz.muni.physics.pdr.backend.repository.plugin.PluginRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.function.Predicate;

/**
 * @author Michal Krajčovič
 * @version 1.0
 * @since 14/04/16
 */
@Component
public class PluginManagerImpl implements PluginManager {

    @Autowired
    private PluginRepository pluginRepository;

    @Override
    public void insert(Plugin entity) {
        throw new UnsupportedOperationException("Insert plugins manually.");
    }

    @Override
    public void delete(Plugin entity) {
        throw new UnsupportedOperationException("Delete plugins manually.");
    }


    @Override
    public Collection<Plugin> getAll() {
        return pluginRepository.getAll();
    }

    @Override
    public Plugin searchFor(Predicate<Plugin> predicate) {
        return pluginRepository.searchFor(predicate);
    }

    @Override
    public Collection<Plugin> searchForAll(Predicate<Plugin> predicate) {
        return pluginRepository.searchForAll(predicate);
    }

    @Override
    public Plugin getById(String s) {
        return pluginRepository.getById(s);
    }
}
