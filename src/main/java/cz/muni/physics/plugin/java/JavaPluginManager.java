package cz.muni.physics.plugin.java;

import cz.muni.physics.java.PhotometricData;
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

    public List<PhotometricData> startPlugins(List<String> starNames){
        List<PhotometricData> data = new ArrayList<>();
        for(Plugin plugin : plugins){
            data.addAll(plugin.getDataByName(starNames.get(0)));
        }

        return data;
    }

    public void loadAllPlugins() {
        for (File file : pluginJars) {
            plugins.add(pluginLoader.load(file));
        }
    }

    public File[] getPluginJars() {
        return pluginJars;
    }

    public void setPluginJars(File[] pluginJars) {
        this.pluginJars = pluginJars;
    }

}
