package cz.muni.physics.pdr.repository.plugin;

import cz.muni.physics.pdr.entity.Plugin;
import cz.muni.physics.pdr.plugin.PluginManagerException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
        return plugins;
    }
}
