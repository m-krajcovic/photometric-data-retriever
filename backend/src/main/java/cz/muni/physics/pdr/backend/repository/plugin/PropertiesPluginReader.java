package cz.muni.physics.pdr.backend.repository.plugin;


import cz.muni.physics.pdr.backend.entity.Plugin;
import cz.muni.physics.pdr.backend.exception.PluginReaderException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Class for reading plugin information from properties file
 * @author Michal Krajčovič
 * @version 1.0
 * @since 13/04/16
 */
class PropertiesPluginReader implements PluginReader {

    private File pluginDir;

    PropertiesPluginReader(File pluginDir) {
        this.pluginDir = pluginDir;
    }

    @Override
    public Plugin readPlugin() throws PluginReaderException {
        Properties props = new Properties();
        try (InputStream is = new FileInputStream(new File(pluginDir, "plugin.properties"))) {
            props.load(is);
        } catch (IOException e) {
            throw new PluginReaderException("There was a problem loading plugin properties file in", pluginDir.getPath(), e);
        }
        String commands = props.getProperty("command");
        String mainFileName = props.getProperty("mainFile");
        if (mainFileName == null || commands == null) {
            throw new PluginReaderException("There are some properties missing inside " + pluginDir.getPath() + "plugin.properties");
        }
        File mainFile = new File(pluginDir, mainFileName);
        if (!mainFile.exists()) {
            throw new PluginReaderException("Main file " + mainFileName + " was not found in", pluginDir.getPath());
        }
        return new Plugin(pluginDir.getName(), mainFile.getPath(), commands.split(";"));
    }
}
