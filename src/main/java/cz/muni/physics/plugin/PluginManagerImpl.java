package cz.muni.physics.plugin;

import cz.muni.physics.model.Plugin;
import cz.muni.physics.utils.PropUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * @author Michal Krajčovič
 * @version 1.0
 * @since 01/04/16
 */
public class PluginManagerImpl implements PluginManager {

    private final static Logger logger = LogManager.getLogger(PluginManagerImpl.class);

    public List<Plugin> getAvailablePlugins() throws PluginManagerException {
        List<Plugin> result = new ArrayList<>();
        String pluginsDirPath = PropUtils.get("plugin.dir.path");
        File dir = new File(pluginsDirPath);
        String[] dirs = dir.list((file, name) -> new File(file, name).isDirectory());
        for (String pluginDirName : dirs) {
            String pluginDirPath = pluginsDirPath + pluginDirName + "/";
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
                Plugin plugin = new Plugin(name, mainFile, command, pluginDirPath);
                result.add(plugin);
            } else {
                logger.debug("Plugin not found inside " + pluginDirPath);
            }
        }
        return result;
    }
}
