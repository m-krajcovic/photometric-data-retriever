package cz.muni.physics.pdr.backend.repository.plugin;

import cz.muni.physics.pdr.backend.exception.PluginReaderException;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Michal Krajčovič
 * @version 1.0
 * @since 13/04/16
 */
public abstract class PluginReaderFactory {
    public static PluginReader getReader(File pluginDir) throws PluginReaderException {
        if (pluginDir == null) {
            throw new IllegalArgumentException("pluginDir cannot be null.");
        }
        if (!pluginDir.isDirectory()) {
            throw new IllegalArgumentException("File pluginDir is not directory.");
        }
        File[] files = pluginDir.listFiles();
        if (files != null) {
            Set<String> fileSet = new HashSet<>();
            for (File file : files) {
                fileSet.add(file.getName());
            }
            if (fileSet.contains("plugin.xml")) {

            } else if (fileSet.contains("plugin.json")) {

            } else if (fileSet.contains("plugin.properties")) {
                return new PropertiesPluginReader(pluginDir);
            }
        }
        throw new PluginReaderException("No plugin config file found in ", pluginDir.getAbsolutePath());
    }
}
