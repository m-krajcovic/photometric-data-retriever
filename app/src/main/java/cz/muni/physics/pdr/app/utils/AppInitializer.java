package cz.muni.physics.pdr.app.utils;

import cz.muni.physics.pdr.app.javafx.PreloaderHandlerEvent;
import cz.muni.physics.pdr.backend.repository.starsurvey.StarSurveyRepositoryImpl;
import javafx.application.Application;
import javafx.scene.control.Alert;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    @Autowired
    private ScreenConfig app;
    @Value("${plugins.dir.path:${default.plugins.dir.path}}")
    private String pluginsDirPath;
    @Value("${app.data.dir.path:${default.app.data.dir.path}}")
    private String dataDirPath;
    @Value("${starsurveys.file.path:${default.starsurveys.file.path}}")
    private String starSurveysFilePath;

    private List<Exception> initExceptions = new ArrayList<>();
    private List<String> initErrors = new ArrayList<>();

    public void initialize(Application mainApp) {
        logger.debug("Initializing mainApp.");

        File dataDir = new File(dataDirPath);
        if (!dataDir.exists()) {
            if (!dataDir.mkdir()) {
                initExceptions.add(new RuntimeException("Failed to create app data directory"));
            }
        }

        File pluginsDir = new File(pluginsDirPath);
        mainApp.notifyPreloader(PreloaderHandlerEvent.PLUGIN_FOLDER_CHECK);
        logger.debug("Checking if app data exists");
        if (!pluginsDir.exists()) {
            logger.debug("Plugins folder not found, creating new one.");
            if (!pluginsDir.mkdir()) {
                initExceptions.add(new RuntimeException("Failed to create plugins directory"));
            }
        }


        File starSurveysFile = new File(starSurveysFilePath);
        if (!starSurveysFile.exists()) {
            try (InputStream inputStream = StarSurveyRepositoryImpl.class.getResourceAsStream("/star_surveys.xml");
                 OutputStream outputStream = new FileOutputStream(starSurveysFile)) {
                int read;
                byte[] bytes = new byte[1024];
                while ((read = inputStream.read(bytes)) != -1) {
                    outputStream.write(bytes, 0, read);
                }
            } catch (IOException e) {
                initExceptions.add(new RuntimeException("Failed to copy default star surveys config to " + starSurveysFilePath, e));
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
