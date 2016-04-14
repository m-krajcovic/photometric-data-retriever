package cz.muni.physics.pdr.plugin;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Michal Krajčovič
 * @version 1.0
 * @since 13/04/16
 */
public abstract class PluginReaderFactory {
    public static PluginReader getReader(File pluginDir) {
        if (pluginDir == null) {
            throw new IllegalArgumentException("pluginDir cannot be null.");
        }
        if (!pluginDir.isDirectory()) {
            throw new IllegalArgumentException("File pluginDir is not directory.");
        }
        File[] files = pluginDir.listFiles();
        if (files != null) {
            Map<String, File> fileMap = new HashMap<>();
            for (File file : files) {
                fileMap.put(file.getName(), file);
            }

            if (fileMap.containsKey("plugin.xml")) {

            } else if (fileMap.containsKey("plugin.json")) {

            } else if (fileMap.containsKey("plugin.properties")) {
                return new PropertiesPluginReader(pluginDir);
            }
        }
        return null;
    }
}
