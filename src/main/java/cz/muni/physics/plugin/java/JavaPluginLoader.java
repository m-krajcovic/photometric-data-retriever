package cz.muni.physics.plugin.java;

import cz.muni.physics.java.Plugin;
import cz.muni.physics.plugin.PluginLoader;
import cz.muni.physics.utils.FXMLUtil;
import cz.muni.physics.utils.PropUtils;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;

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

    private final static Logger logger = Logger.getLogger(JavaPluginLoader.class);

    //Class loader -> v manifeste od jaru bude prilozena trieda pluginu :)
    public Plugin load(@NotNull File file) {
        logger.debug("Loading plugin from " + file.getName());
        try {
            URLClassLoader loader = URLClassLoader.newInstance(new URL[]{file.toURI().toURL()});
            URL manifestUrl = loader.findResource("META-INF/MANIFEST.MF");
            Manifest manifest = new Manifest(manifestUrl.openStream());
            Attributes attributes = manifest.getMainAttributes();
            String pluginClass = attributes.getValue("Plugin-Class");
            if(pluginClass == null || pluginClass.isEmpty()){
                logger.error("Manifest doesn't contain Plugin-Class attribute");
                return null;
            }
            logger.debug("Loading class " + pluginClass);
            Object obj = loader.loadClass(pluginClass).newInstance();
            if (obj instanceof Plugin) {
                return (Plugin) obj;
            } else {
                logger.error("Class " + pluginClass + " does not implement " + Plugin.class.getCanonicalName());
            }
        } catch (InstantiationException e) {
            FXMLUtil.INSTANCE.showExceptionAlert();
        } catch (IllegalAccessException e) {
            logger.error("Access to read plugin " + file.getName() + " file denied.");
            FXMLUtil.INSTANCE.showExceptionAlert();
        } catch (ClassNotFoundException e) {
            logger.error("Class specified in Plugin-Class attribute was not found inside .jar file");
            FXMLUtil.INSTANCE.showExceptionAlert();
        } catch (MalformedURLException e) {
            logger.error("Provided plugin file name " + file.getName() + " is malformed");
            FXMLUtil.INSTANCE.showExceptionAlert();
        } catch (IOException e) {
            logger.error("Something is wrong", e);
        }
        return null;
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
