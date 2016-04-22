package cz.muni.physics.pdr.backend.repository.plugin;

import cz.muni.physics.pdr.backend.entity.Plugin;
import cz.muni.physics.pdr.backend.exception.PluginReaderException;
import cz.muni.physics.pdr.backend.repository.FileWatcher;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author Michal Krajčovič
 * @version 1.0
 * @since 14/04/16
 */
public class PluginRepositoryImpl implements PluginRepository {

    private final static Logger logger = LogManager.getLogger(PluginRepositoryImpl.class);

    private File pluginsFolder;
    private FileWatcher fileWatcher;
    private Map<String, Plugin> plugins;

    public PluginRepositoryImpl(File pluginsFolder) {
        this.pluginsFolder = pluginsFolder;
        fileWatcher = new FileWatcher(pluginsFolder);
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
    public Collection<Plugin> getAll() {
        checkAndLoadPlugins();
        List<Plugin> newList = new ArrayList<>(plugins.values().size());
        plugins.values().forEach(plugin -> newList.add(new Plugin(plugin)));
        return newList;
    }

    @Override
    public Plugin getById(String s) {
        checkAndLoadPlugins();
        if (plugins.containsKey(s)) {
            return new Plugin(plugins.get(s));
        } else {
            return null;
        }
    }

    @Override
    public Plugin searchFor(Predicate<Plugin> predicate) {
        checkAndLoadPlugins();
        Optional<Plugin> optional = plugins.values().stream().filter(predicate).findFirst();
        if (optional.isPresent()) {
            return new Plugin(optional.get());
        }
        return null;
    }

    @Override
    public Collection<Plugin> searchForAll(Predicate<Plugin> predicate) {
        checkAndLoadPlugins();
        List<Plugin> result = plugins.values().stream().filter(predicate).collect(Collectors.toList());
        List<Plugin> newList = new ArrayList<>(result.size());
        result.forEach(plugin -> newList.add(new Plugin(plugin)));
        return newList;
    }

    private void checkAndLoadPlugins() {
        if (plugins == null) {
            logger.debug("Plugins were not loaded yet. Loading plugins from {}", pluginsFolder.getAbsolutePath());
            loadPlugins();
        } else if (fileWatcher.isFileUpdated()) {
            logger.debug("Plugins folder has changed. Loading plugins from {}", pluginsFolder.getAbsolutePath());
            loadPlugins();
        } else {
            logger.debug("No outside change in plugins folder. Using cached copy.");
        }
    }

    private synchronized void loadPlugins() {
        Map<String, Plugin> tempPlugins = new HashMap<>();
        String[] dirs = pluginsFolder.list((file, name) -> new File(file, name).isDirectory());
        for (String pluginDirName : dirs) {
            String pluginDirPath = pluginsFolder.getAbsolutePath() + File.separator + pluginDirName + File.separator;
            try {
                PluginReader reader = PluginReaderFactory.getReader(new File(pluginDirPath));
                Plugin plugin = reader.readPlugin();
                logger.debug("Loaded plugin {}", plugin.getName());
                tempPlugins.put(plugin.getName(), plugin);
            } catch (PluginReaderException exc) {
                logger.error("Failed loading plugin from {}", pluginDirPath, exc);
            }
        }
        plugins = new HashMap<>(tempPlugins);
    }
}
