package cz.muni.physics.plugin.java;

import cz.muni.physics.java.Plugin;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Michal Krajčovič
 * @version 1.0
 * @since 26/03/16
 */
public class JavaPluginManager {

    @Autowired
    private JavaPluginLoader pluginLoader;

    private File[] pluginJars;
    private List<Plugin> plugins = new ArrayList<>();

    public void loadPlugin(File file) throws JavaPluginLoaderException {
        plugins.add(pluginLoader.load(file));
    }

    public List<Plugin> getPlugins() {
        return plugins;
    }

    public File[] getPluginJars() {
        return pluginJars;
    }

    public void setPluginJars(File[] pluginJars) {
        this.pluginJars = pluginJars;
    }

}
