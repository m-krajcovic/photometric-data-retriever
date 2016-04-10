package cz.muni.physics.pdr.plugin;

import cz.muni.physics.pdr.model.Plugin;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

/**
 * @author Michal Krajčovič
 * @version 1.0
 * @since 01/04/16
 */
public class PluginLoaderImpl implements PluginLoader {

    private final static Logger logger = LogManager.getLogger(PluginLoaderImpl.class);

    private String pluginsFolderPath;

    public PluginLoaderImpl(String pluginsFolderPath) {
        this.pluginsFolderPath = pluginsFolderPath;
    }

    public Set<Plugin> getAvailablePlugins() throws PluginManagerException {
        Set<Plugin> result = new HashSet<>();
        File dir = new File(pluginsFolderPath);
        String[] dirs = dir.list((file, name) -> new File(file, name).isDirectory());
        for (String pluginDirName : dirs) {
            String pluginDirPath = pluginsFolderPath + File.separator + pluginDirName + File.separator;
            File pluginProps = new File(pluginDirPath, "plugin.properties");
            if (pluginProps.exists()) {
                logger.debug("Found plugin properties in: " + pluginDirPath);
                Properties props = new Properties();
                try (InputStream is = new FileInputStream(pluginProps)) {
                    props.load(is);
                } catch (FileNotFoundException e) {
                    throw new PluginManagerException("FileNotFoundException", e);
                } catch (IOException e) {
                    throw new PluginManagerException("IOException", e);
                }
                String name = props.getProperty("name");
                String mainFile = props.getProperty("main.file");
                String command = props.getProperty("command");
                if (name == null || mainFile == null || command == null) {
                    throw new PluginManagerException("There are some properties missing inside " + pluginDirPath + "plugin.properties");
                }
                Plugin plugin = new Plugin(name, mainFile, command);
                result.add(plugin);
            } else {
                logger.debug("Plugin not found inside " + pluginDirPath);
            }
        }
        return result;
    }
}
