package cz.muni.physics.pdr.backend.repository.plugin;


import cz.muni.physics.pdr.backend.entity.Plugin;
import cz.muni.physics.pdr.backend.plugin.PluginManagerException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
    public Plugin readPlugin() throws PluginManagerException {
        Properties props = new Properties();
        try (InputStream is = new FileInputStream(new File(pluginDir, "plugin.properties"))) {
            props.load(is);
        } catch (FileNotFoundException e) {
            throw new PluginManagerException("FileNotFoundException", e); //todo
        } catch (IOException e) {
            throw new PluginManagerException("IOException", e);
        }
        String commands = props.getProperty("command");
        String mainFileName = props.getProperty("mainFile");
        if (mainFileName == null || commands == null) {
            throw new PluginManagerException("There are some properties missing inside " + pluginDir.getPath() + "plugin.properties");
        }
        File mainFile = new File(pluginDir, mainFileName);
        if (!mainFile.exists()) {
            throw new PluginManagerException("Main file " + mainFileName + " was not found.");
        }
        return new Plugin(pluginDir.getName(), mainFile.getPath(), commands.split(";"));
    }

    @Override
    public void setPluginDir(File pluginDir) {
        this.pluginDir = pluginDir;
    }
}
