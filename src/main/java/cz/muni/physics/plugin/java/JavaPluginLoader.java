package cz.muni.physics.plugin.java;

import cz.muni.physics.java.Plugin;
import cz.muni.physics.plugin.PluginLoader;
import cz.muni.physics.utils.PropUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

/**
 * @author Michal Krajčovič
 * @version 1.0
 * @since 25/03/16
 */
public class JavaPluginLoader implements PluginLoader {

    private final static Logger logger = LogManager.getLogger(JavaPluginLoader.class);

    public Plugin load(File file) throws JavaPluginLoaderException {
        logger.debug("Loading plugin from " + file.getName());
        URLClassLoader loader;
        try {
            loader = URLClassLoader.newInstance(new URL[]{file.toURI().toURL()});
        } catch (MalformedURLException e) {
            throw new JavaPluginLoaderException("Provided plugin file URL " + file.getName() + " is malformed", e);
        }

        URL manifestUrl = loader.findResource("META-INF/MANIFEST.MF");
        Manifest manifest;
        try {
            manifest = new Manifest(manifestUrl.openStream());
        } catch (IOException e) {
            throw new JavaPluginLoaderException("Can't read manifest file from " + file.getName(), e);
        }

        Attributes attributes = manifest.getMainAttributes();
        String pluginClass = attributes.getValue("Plugin-Class");
        if (pluginClass == null || pluginClass.isEmpty()) {
            throw new JavaPluginLoaderException("Manifest from " + file.getName() + " doesn't contain Plugin-Class attribute");
        }

        Object obj;
        try {
            logger.debug("Loading class " + pluginClass);
            obj = loader.loadClass(pluginClass).newInstance();
        } catch (InstantiationException e) {
            throw new JavaPluginLoaderException("Could not create a new instance of plugin class " + pluginClass, e);
        } catch (IllegalAccessException e) {
            throw new JavaPluginLoaderException("Access to load class " + pluginClass + " denied.", e);
        } catch (ClassNotFoundException e) {
            throw new JavaPluginLoaderException("Class " + pluginClass + " specified in Plugin-Class attribute was not found inside " + file.getName(), e);
        }
        if (obj instanceof Plugin) {
            return (Plugin) obj;
        } else {
            throw new JavaPluginLoaderException("Class " + pluginClass + " does not implement " + Plugin.class.getCanonicalName());
        }
    }

    public File[] getAvailablePluginJars() {
        logger.debug("Searching for available .jar plugins.");
        File dir = new File(PropUtils.get("plugin.dir.path"));
        File[] files = dir.listFiles((dir1, filename) -> {
            return filename.endsWith(".jar");
        });
        logger.debug("Found " + files.length + " .jar plugins");
        return files;
    }
}
