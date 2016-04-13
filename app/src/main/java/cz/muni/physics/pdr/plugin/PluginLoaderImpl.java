package cz.muni.physics.pdr.plugin;

import cz.muni.physics.pdr.model.Plugin;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Michal Krajčovič
 * @version 1.0
 * @since 01/04/16
 */
public class PluginLoaderImpl implements PluginLoader {

    private final static Logger logger = LogManager.getLogger(PluginLoaderImpl.class);

    @Value("${user.home}${plugins.dir.path}")
    private String pluginsFolderPath;

    public PluginLoaderImpl() {
    }

    public Map<String, Plugin> getAvailablePlugins() throws PluginManagerException {
        Map<String, Plugin> resultMap = new HashMap<>();
        File dir = new File(pluginsFolderPath);
        String[] dirs = dir.list((file, name) -> new File(file, name).isDirectory());
        for (String pluginDirName : dirs) {
            String pluginDirPath = pluginsFolderPath + File.separator + pluginDirName + File.separator;
            PluginReader reader = PluginReaderFactory.getReader(new File(pluginDirPath));
            if (reader != null) {
                Plugin plugin = reader.readPlugin();
                resultMap.put(plugin.getName(), plugin);
            }
        }
        return resultMap;
    }
}
