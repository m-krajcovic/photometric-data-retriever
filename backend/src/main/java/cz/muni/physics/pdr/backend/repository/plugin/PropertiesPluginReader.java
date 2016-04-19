package cz.muni.physics.pdr.backend.repository.plugin;


import cz.muni.physics.pdr.backend.entity.Plugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author Michal Krajčovič
 * @version 1.0
 * @since 13/04/16
 */
public class PropertiesPluginReader implements PluginReader {

    private File pluginDir;

    public PropertiesPluginReader(File pluginDir) {
        this.pluginDir = pluginDir;
    }

    @Override
    public Plugin readPlugin() {
        Properties props = new Properties();
        try (InputStream is = new FileInputStream(new File(pluginDir, "plugin.properties"))) {
            props.load(is);
        } catch (IOException e) {
            throw new RuntimeException("There was a problem loading plugin properties file", e);
        }
        String commands = props.getProperty("command");
        String mainFileName = props.getProperty("mainFile");
        if (mainFileName == null || commands == null) {
            throw new RuntimeException("There are some properties missing inside " + pluginDir.getPath() + "plugin.properties");
        }
        File mainFile = new File(pluginDir, mainFileName);
        if (!mainFile.exists()) {
            throw new RuntimeException("Main file " + mainFileName + " was not found.");
        }
        return new Plugin(pluginDir.getName(), mainFile.getPath(), commands.split(";"));
    }

    @Override
    public void setPluginDir(File pluginDir) {
        this.pluginDir = pluginDir;
    }
}
