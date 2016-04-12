package cz.muni.physics.pdr.utils;

import com.thoughtworks.xstream.XStreamException;
import cz.muni.physics.pdr.javafx.PreloaderHandlerEvent;
import cz.muni.physics.pdr.model.Plugin;
import cz.muni.physics.pdr.model.StarSurvey;
import cz.muni.physics.pdr.nameresolver.NameResolverManager;
import cz.muni.physics.pdr.plugin.PluginLoader;
import cz.muni.physics.pdr.plugin.PluginManagerException;
import cz.muni.physics.pdr.storage.DataStorage;
import javafx.application.Application;
import javafx.scene.control.Alert;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Michal Krajčovič
 * @version 1.0
 * @since 09/04/16
 */
@Component
public class AppInitializer {
    private final static Logger logger = LogManager.getLogger(AppInitializer.class);

    @Autowired
    private AppConfig app;
    @Autowired
    private DataStorage dataStorage;
    @Autowired
    private PluginLoader pluginLoader;
    @Autowired
    private NameResolverManager nameResolverManager;
    @Value("${user.home}${plugins.dir.path}")
    private String pluginsDirPath;

    private List<Exception> initExceptions = new ArrayList<>();
    private List<String> initErrors = new ArrayList<>();

    public void initialize(Application mainApp) {
        logger.debug("Initializing mainApp.");
        File dir = new File(pluginsDirPath);
        mainApp.notifyPreloader(PreloaderHandlerEvent.PLUGIN_FOLDER_CHECK);
        logger.debug("Checking if app data exists");
        if (!dir.exists()) {
            logger.debug("Plugin folder not found, creating new one.");
            dir.mkdir();
        }

        mainApp.notifyPreloader(PreloaderHandlerEvent.CHECKING_STAR_SURVEYS);
        logger.debug("Loading star surveys.");
        try {
            app.getStarSurveys().addAll(dataStorage.loadStarSurveys()); // TODO catch here // if this fails -> load default
        } catch (XStreamException exc) {
            logger.error("Could not load star surveys from xml file", exc);
            initExceptions.add(exc);
        }

        mainApp.notifyPreloader(PreloaderHandlerEvent.LOADING_PLUGINS);
        logger.debug("Loading plugins from {}", pluginsDirPath);
        Map<String, Plugin> availablePlugins = null;
        try {
            availablePlugins = pluginLoader.getAvailablePlugins();
        } catch (PluginManagerException e) {
            logger.error(e.getMessage(), e);
            initExceptions.add(e);
        }
        if (availablePlugins == null || availablePlugins.isEmpty()) {
            logger.debug("No plugins found inside plugins folder");
            initErrors.add("There are 0 plugins inside plugins folder.");
        } else {
            for (StarSurvey record : app.getStarSurveys()) {
                if(record.getPlugin() == null || record.getPlugin().getName().isEmpty()) continue;
                if (!availablePlugins.containsKey(record.getPlugin().getName())) {
                    initErrors.add(record.getPlugin().getName() + " is not available inside plugins folder.");
                    record.setPlugin(null);
                } else {
                    record.setPlugin(availablePlugins.get(record.getPlugin().getName()));
                }
            }
            app.getPlugins().addAll(availablePlugins.values());
        }

        mainApp.notifyPreloader(PreloaderHandlerEvent.CHECKING_SESAME);
        nameResolverManager.getAvailableNameResolvers().forEach((resolver, available) -> {
                    if (!available) {
                        logger.warn(resolver.getClass().getCanonicalName() + " is not available.");
                        initErrors.add(resolver.getClass().getCanonicalName() + " is not available. Check your internet connection.");
                    }
                }
        );
    }

    public void showInitExceptions() {
        for (Exception initException : initExceptions) {
            FXMLUtils.showExceptionAlert("What a mess!", "Something went wrong during initialization.", initException.getMessage(), initException);
        }
    }

    public void showInitErrors() {
        for (String error : initErrors) {
            FXMLUtils.showAlert("Error", null, error, Alert.AlertType.ERROR);
        }
    }
}
