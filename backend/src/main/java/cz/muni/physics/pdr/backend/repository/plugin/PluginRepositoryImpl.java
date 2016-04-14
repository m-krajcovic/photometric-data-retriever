package cz.muni.physics.pdr.backend.repository.plugin;

import cz.muni.physics.pdr.backend.entity.Plugin;
import cz.muni.physics.pdr.backend.plugin.PluginManagerException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author Michal Krajčovič
 * @version 1.0
 * @since 14/04/16
 */
@Component
public class PluginRepositoryImpl implements PluginRepository {

    @Value("${user.home}${plugins.dir.path}")
    private String pluginsFolderPath;

    List<Plugin> plugins;

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
        if (plugins == null) {
            plugins = new ArrayList<>();
            File dir = new File(pluginsFolderPath);
            String[] dirs = dir.list((file, name) -> new File(file, name).isDirectory());
            for (String pluginDirName : dirs) {
                String pluginDirPath = pluginsFolderPath + File.separator + pluginDirName + File.separator;
                PluginReader reader = PluginReaderFactory.getReader(new File(pluginDirPath));
                if (reader != null) {
                    Plugin plugin = null;
                    try {
                        plugin = reader.readPlugin();
                    } catch (PluginManagerException e) {
                        e.printStackTrace();
                    }
                    plugins.add(plugin);
                }
            }
        }
        List<Plugin> newList = new ArrayList<>();
        plugins.forEach(plugin -> newList.add(new Plugin(plugin)));
        return newList;
    }

    @Override
    public Plugin getById(String s) {
        return searchFor(p -> p.getName().equalsIgnoreCase(s));
    }

    @Override
    public Plugin searchFor(Predicate<Plugin> predicate) {
        Optional<Plugin> optional = getAll().stream().filter(predicate).findFirst();
        if(optional.isPresent()){
            return new Plugin(optional.get());
        }
        return null;
    }

    @Override
    public Collection<Plugin> searchForAll(Predicate<Plugin> predicate) {
        List<Plugin> result = this.plugins.stream().filter(predicate).collect(Collectors.toList());
        List<Plugin> newList = new ArrayList<>();
        result.forEach(plugin -> newList.add(new Plugin(plugin)));
        return newList;
    }
}
