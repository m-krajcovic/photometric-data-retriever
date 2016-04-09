package cz.muni.physics.utils;

import com.thoughtworks.xstream.XStreamException;
import cz.muni.physics.javafx.PreloaderHandlerEvent;
import cz.muni.physics.model.Plugin;
import cz.muni.physics.model.StarSurvey;
import cz.muni.physics.plugin.PluginLoader;
import cz.muni.physics.plugin.PluginManagerException;
import cz.muni.physics.sesame.SesameClient;
import cz.muni.physics.storage.DataStorage;
import javafx.application.Application;
import javafx.scene.control.Alert;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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
    private SesameClient sesameClient;

    private List<Exception> initExceptions = new ArrayList<>();
    private List<String> initErrors = new ArrayList<>();


    public void initialize(Application mainApp){
        logger.debug("Initializing mainApp.");
        File dir = dataStorage.getPluginsDir();
        mainApp.notifyPreloader(PreloaderHandlerEvent.PLUGIN_FOLDER_CHECK);
        if (!dir.exists()) {
            logger.debug("Plugin folder not found, creating new one.");
            dir.mkdir();
        }

        mainApp.notifyPreloader(PreloaderHandlerEvent.CHECKING_STAR_SURVEYS);
        try {
            app.getStarSurveys().addAll(dataStorage.loadStarSurveys()); // TODO catch here
        } catch (XStreamException exc) {
            logger.error("Could not load star surveys from xml file");
            initExceptions.add(exc);
        }

        mainApp.notifyPreloader(PreloaderHandlerEvent.LOADING_PLUGINS);
        Set<Plugin> availablePlugins = null;
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
                if (!availablePlugins.contains(record.getPlugin())) {
                    initErrors.add(record.getPlugin().getName() + " is not available inside plugins folder.");
                    record.setPlugin(null);
                }
            }
            app.getPlugins().addAll(availablePlugins);
        }

        mainApp.notifyPreloader(PreloaderHandlerEvent.CHECKING_SESAME);
        if (!sesameClient.isAvailable()) {
            logger.debug("Sesame Name Resolver is not available.");
            initErrors.add("Sesame Name Resolver is not available. Check your internet connection.");
        }
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
