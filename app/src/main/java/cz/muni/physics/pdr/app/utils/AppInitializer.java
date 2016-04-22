package cz.muni.physics.pdr.app.utils;

import cz.muni.physics.pdr.app.javafx.PreloaderHandlerEvent;
import cz.muni.physics.pdr.backend.repository.starsurvey.StarSurveyRepositoryImpl;
import javafx.application.Application;
import javafx.scene.control.Alert;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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

    private File appDataDir;
    private File pluginsDir;
    private File starSurveysFile;
    private File vsxDatFile;

    private List<Exception> initExceptions = new ArrayList<>();
    private List<String> initErrors = new ArrayList<>();


    public AppInitializer(File appDataDir, File pluginsDir, File starSurveysFile, File vsxDatFile) {
        this.appDataDir = appDataDir;
        this.pluginsDir = pluginsDir;
        this.starSurveysFile = starSurveysFile;
        this.vsxDatFile = vsxDatFile;
    }

    public void initialize(Application mainApp) {
        logger.debug("Initializing mainApp.");

        if (!appDataDir.exists()) {
            if (!appDataDir.mkdir()) {
                initExceptions.add(new RuntimeException("Failed to create app data directory"));
            }
        }

        mainApp.notifyPreloader(PreloaderHandlerEvent.PLUGIN_FOLDER_CHECK);
        logger.debug("Checking if app data exists");
        if (!pluginsDir.exists()) {
            logger.debug("Plugins folder not found, creating new one.");
            if (!pluginsDir.mkdir()) {
                initExceptions.add(new RuntimeException("Failed to create plugins directory"));
            }
        }


        if (!starSurveysFile.exists()) {
            try (InputStream inputStream = StarSurveyRepositoryImpl.class.getResourceAsStream("/star_surveys.xml");
                 OutputStream outputStream = new FileOutputStream(starSurveysFile)) {
                int read;
                byte[] bytes = new byte[1024];
                while ((read = inputStream.read(bytes)) != -1) {
                    outputStream.write(bytes, 0, read);
                }
            } catch (IOException e) {
                initExceptions.add(new RuntimeException("Failed to copy default star surveys config to " + starSurveysFile.getAbsolutePath(), e));
            }
        }
        
        // TODO check vsx dat file

//        mainApp.notifyPreloader(PreloaderHandlerEvent.CHECKING_SESAME);
//        sesameNameResolver.getAvailableStarResolvers().forEach((resolver, available) -> {
//                    if (!available) {
//                        logger.warn(resolver.getClass().getCanonicalName() + " is not available.");
//                        initErrors.add(resolver.getClass().getCanonicalName() + " is not available.");
//                    }
//                }
//        );
    }

    public void showInitExceptions() {
        for (Exception initException : initExceptions) {
            FXMLUtils.showExceptionAlert("What a mess!", "Something went wrong during initialization.", initException.getMessage(), initException);
        }
    }

    public void showInitErrors() {
        for (String error : initErrors) {
            FXMLUtils.alert("Error", null, error, Alert.AlertType.ERROR);
        }
    }
}
