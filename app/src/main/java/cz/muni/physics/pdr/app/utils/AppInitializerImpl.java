package cz.muni.physics.pdr.app.utils;

import cz.muni.physics.pdr.app.javafx.PreloaderHandlerEvent;
import cz.muni.physics.pdr.backend.repository.starsurvey.StarSurveyRepositoryImpl;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.zip.GZIPInputStream;

/**
 * @author Michal Krajčovič
 * @version 1.0
 * @since 09/04/16
 */
public class AppInitializerImpl implements AppInitializer {
    private final static Logger logger = LogManager.getLogger(AppInitializerImpl.class);

    private File appDataDir;
    private File pluginsDir;
    private File starSurveysFile;
    private File vsxDatFile;
    private String vsxDownloadUrl;
    private boolean checkOutdated;

    private List<Exception> initExceptions = new ArrayList<>();
    private List<String> initErrors = new ArrayList<>();
    private List<Consumer<Alert>> confirmations = new ArrayList<>();


    public AppInitializerImpl(File appDataDir, File pluginsDir, File starSurveysFile, File vsxDatFile, String vsxDownloadUrl, boolean checkOutdated) {
        this.appDataDir = appDataDir;
        this.pluginsDir = pluginsDir;
        this.starSurveysFile = starSurveysFile;
        this.vsxDatFile = vsxDatFile;
        this.vsxDownloadUrl = vsxDownloadUrl;
        this.checkOutdated = checkOutdated;
    }

    @Override
    public void initialize(Application mainApp) {
        logger.debug("Initializing Application {}", mainApp.getClass());

        mainApp.notifyPreloader(PreloaderHandlerEvent.DATA_DIR_CHECK);
        if (!appDataDir.exists()) {
            logger.debug("Checking if app data folder exists already");
            if (!appDataDir.mkdir()) {
                initErrors.add("Failed to create app data directory");
            }
        }

        mainApp.notifyPreloader(PreloaderHandlerEvent.PLUGIN_FOLDER_CHECK);
        logger.debug("Checking if app data exists");
        if (!pluginsDir.exists()) {
            logger.debug("Plugins folder not found, creating new one.");
            if (!pluginsDir.mkdir()) {
                initErrors.add("Failed to create plugins directory");
            }
        }

        mainApp.notifyPreloader(PreloaderHandlerEvent.STAR_SURVEYS_CHECK);
        if (!starSurveysFile.exists()) {
            logger.debug("Star surveys config file does not exist");
            confirmations.add(alert -> {
                alert.setTitle("Star surveys config file not found.");
                alert.setHeaderText("Could not find file " + starSurveysFile.getAbsolutePath());
                alert.setContentText("Do you want to load default star survey settings?");
                Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    loadDefaultStarSurveysFile();
                }
            });
        }

        mainApp.notifyPreloader(PreloaderHandlerEvent.VSX_DAT_CHECK);
        if (!vsxDatFile.exists()) {
            logger.debug("vsx.dat file does not exist");
            confirmations.add(alert -> {
                alert.setTitle("VSX dat file not found.");
                alert.setHeaderText("Could not find file " + vsxDatFile);
                alert.setContentText("Do you want to download it now?");
                Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    downloadVsxDatFile();
                }
            });
        } else if (checkOutdated) {
            logger.debug("Checking if vsx.dat file is up to date");
            Calendar c = Calendar.getInstance();
            c.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
            c.set(Calendar.HOUR_OF_DAY, 8);
            if (vsxDatFile.lastModified() < c.getTimeInMillis()) {
                logger.debug("vsx.dat file is outdated");
                confirmations.add(alert -> {
                    alert.setTitle("VSX dat file is outdated.");
                    alert.setContentText("Do you want to download current version?");
                    Optional<ButtonType> result = alert.showAndWait();
                    if (result.isPresent() && result.get() == ButtonType.OK) {
                        downloadVsxDatFile();
                    }
                });
            } else {
                logger.debug("vsx.dat file is up to date");
            }
        } else {
            logger.debug("Checking for outdated vsx.dat file is turned off, skipping");
        }
    }

    private void loadDefaultStarSurveysFile() {
        logger.debug("Started loading default star surveys file");
        try (InputStream inputStream = StarSurveyRepositoryImpl.class.getResourceAsStream("/star_surveys.xml"); //async
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

    private void downloadVsxDatFile() {
        logger.debug("Starting to download vsx.dat file");
        File tempFile = null;
        try {
            URL url = new URL(vsxDownloadUrl);
            tempFile = new File(FileUtils.getTempDirectory(), "vsx-" + System.currentTimeMillis() + ".dat.gz"); //async
            FileUtils.copyURLToFile(url, tempFile);
            try (GZIPInputStream gzis = new GZIPInputStream(new FileInputStream(tempFile));
                 FileOutputStream out = new FileOutputStream(vsxDatFile)) {
                byte[] buffer = new byte[1024];
                int len;
                while ((len = gzis.read(buffer)) > 0) {
                    out.write(buffer, 0, len);
                }
                logger.debug("Successfully downloaded vsx.dat file");
            }
        } catch (IOException e) {
            logger.error("Failed to download vsx.dat file", e);
            initErrors.add("Failed to download vsx.dat file. Try downloading it manually and put it into pdr app data folder");
        } finally {
            if (tempFile != null && tempFile.exists()) {
                if (tempFile.delete()) {
                    logger.debug("Cleaned up temp file {}", tempFile.getAbsoluteFile());
                } else {
                    logger.error("Could not delete temp file {}", tempFile.getAbsoluteFile());
                }
            }
        }
    }

    private void showInitExceptions() {
        for (Exception initException : initExceptions) {
            FXMLUtils.showExceptionAlert("What a mess!", "Something went wrong during initialization.", initException.getMessage(), initException);
        }
    }

    private void showInitErrors() {
        for (String message : initErrors) {
            FXMLUtils.alert("Error", null, message, Alert.AlertType.ERROR);
        }
    }

    private void showAlerts() {
        for (Consumer<Alert> consumer : confirmations) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            consumer.accept(alert);
        }
    }

    @Override
    public void start() {
        if (!Platform.isFxApplicationThread()) {
            throw new IllegalStateException("This method must be called only from JavaFX Application Thread");
        }
        showInitExceptions();
        showInitErrors();
        showAlerts();
    }
}
