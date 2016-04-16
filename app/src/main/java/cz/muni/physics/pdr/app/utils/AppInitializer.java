package cz.muni.physics.pdr.app.utils;

import cz.muni.physics.pdr.app.javafx.PreloaderHandlerEvent;
import cz.muni.physics.pdr.backend.entity.StellarObjectName;
import cz.muni.physics.pdr.backend.resolver.StarResolverManager;
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

/**
 * @author Michal Krajčovič
 * @version 1.0
 * @since 09/04/16
 */
@Component
public class AppInitializer {
    private final static Logger logger = LogManager.getLogger(AppInitializer.class);

    @Autowired
    private ScreenConfig app;
    @Autowired
    private StarResolverManager<StellarObjectName> nameResolverManager;
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
            logger.debug("PluginModel folder not found, creating new one.");
            dir.mkdir();
        }

//        mainApp.notifyPreloader(PreloaderHandlerEvent.CHECKING_STAR_SURVEYS);
//        logger.debug("Loading star surveys.");
//        try {
//            app.getStarSurveys().addAll(dataStorage.loadStarSurveys()); // TODO catch here // if this fails -> load default
//        } catch (XStreamException exc) {
//            logger.error("Could not load star surveys from xml file", exc);
//            initExceptions.add(exc);
//        }

//        mainApp.notifyPreloader(PreloaderHandlerEvent.LOADING_PLUGINS);
//        logger.debug("Loading plugins from {}", pluginsDirPath);
//        Map<String, Plugin> availablePlugins = null;
//        try {
//            availablePlugins = pluginLoader.getAvailablePlugins();
//        } catch (PluginManagerException e) {
//            logger.error(e.getMessage(), e);
//            initExceptions.add(e);
//        }
//        if (availablePlugins == null || availablePlugins.isEmpty()) {
//            logger.debug("No plugins found inside plugins folder");
//            initErrors.add("There are 0 plugins inside plugins folder.");
//        } else {
//            for (StarSurvey record : app.getStarSurveys()) {
//                if(record.getPlugin() == null || record.getPlugin().getName().isEmpty()) continue;
//                if (!availablePlugins.containsKey(record.getPlugin().getName())) {
//                    initErrors.add(record.getPlugin().getName() + " is not available inside plugins folder.");
//                    record.setPlugin(null);
//                } else {
//                    record.setPlugin(availablePlugins.get(record.getPlugin().getName()));
//                }
//            }
//            app.getPlugins().addAll(availablePlugins.values());
//        }

        // TODO checkc vsx dat file

        mainApp.notifyPreloader(PreloaderHandlerEvent.CHECKING_SESAME);
        nameResolverManager.getAvailableStarResolvers().forEach((resolver, available) -> {
                    if (!available) {
                        logger.warn(resolver.getClass().getCanonicalName() + " is not available.");
                        initErrors.add(resolver.getClass().getCanonicalName() + " is not available.");
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
