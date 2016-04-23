package cz.muni.physics.pdr.app.utils;

import cz.muni.physics.pdr.app.javafx.PreloaderHandlerEvent;
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
    private File configFile;
    private File vsxDatFile;
    private String vsxDownloadUrl;
    private boolean checkOutdated;

    private Application mainApp;

    private List<Exception> initExceptions = new ArrayList<>();
    private List<String> initErrors = new ArrayList<>();
    private List<Consumer<Alert>> confirmations = new ArrayList<>();
    private boolean initializeCalled = false;
    private boolean shutdown = false;

    public AppInitializerImpl(File appDataDir, File pluginsDir, File configFile, File vsxDatFile, String vsxDownloadUrl, boolean checkOutdated) {
        this.appDataDir = appDataDir;
        this.pluginsDir = pluginsDir;
        this.configFile = configFile;
        this.vsxDatFile = vsxDatFile;
        this.vsxDownloadUrl = vsxDownloadUrl;
        this.checkOutdated = checkOutdated;
    }

    @Override
    public void initialize(Application mainApp) {
        this.mainApp = mainApp;
        initializeCalled = true;
        logger.debug("Initializing Application {}", mainApp.getClass());

        mainApp.notifyPreloader(PreloaderHandlerEvent.DATA_DIR_CHECK);
        logger.debug("Checking if app data folder {} exists", appDataDir.getAbsoluteFile());
        if (!appDataDir.exists()) {
            logger.debug("App data folder not found, creating new one.");
            if (!appDataDir.mkdir()) {
                initErrors.add("Failed to create app data directory");
            }
        }

        mainApp.notifyPreloader(PreloaderHandlerEvent.PLUGIN_FOLDER_CHECK);
        logger.debug("Checking if plugins folder {} exists", pluginsDir.getAbsoluteFile());
        if (!pluginsDir.exists()) {
            logger.debug("Plugins folder not found, creating a new one");
            if (!pluginsDir.mkdir()) {
                initErrors.add("Failed to create plugins directory");
            }
        }

        mainApp.notifyPreloader(PreloaderHandlerEvent.CONFIG_FILE_CHECK);
        logger.debug("Checking if configuration file {} exists", configFile.getAbsoluteFile());
        if (!configFile.exists()) {
            logger.debug("Configuration file does not exist");
            confirmations.add(alert -> {
                alert.setTitle("Configuration file not found.");
                alert.setHeaderText("Could not find file " + configFile.getAbsolutePath());
                alert.setContentText("Default configuration settings will be loaded.");
                Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    loadDefaultConfigFile();
                } else {
                    logger.debug("Configuration info alert was closed and not confirmed, shutting down app");
                    shutdown = true;
                }
            });
        }

        mainApp.notifyPreloader(PreloaderHandlerEvent.VSX_DAT_CHECK);
        logger.debug("Checking if vsx.dat file {} exists", vsxDatFile.getAbsoluteFile());
        if (!vsxDatFile.exists()) {
            logger.debug("File vsx.dat does not exist");
            confirmations.add(alert -> {
                alert.setAlertType(Alert.AlertType.CONFIRMATION);
                alert.setTitle("File vsx.dat not found.");
                alert.setHeaderText("Could not find file " + vsxDatFile);
                alert.setContentText("Do you want to download it now? If not, application will shut down and you will have to do it manually.");
                Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    downloadVsxDatFile();
                } else {
                    logger.debug("Confirmation alert to download vsx.dat was canceled, application is set to shut down");
                    shutdown = true;
                }
            });
        } else if (checkOutdated) {
            logger.debug("Checking if vsx.dat file is up to date");
            Calendar c = Calendar.getInstance();
            c.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
            c.set(Calendar.HOUR_OF_DAY, 8);
            if (vsxDatFile.lastModified() < c.getTimeInMillis()) {
                logger.debug("File vsx.dat is outdated.");
                confirmations.add(alert -> {
                    alert.setAlertType(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("File vsx.dat is outdated.");
                    alert.setContentText("Do you want to download the current version?");
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

    private void loadDefaultConfigFile() {
        logger.debug("Started loading default config file");
        try (InputStream inputStream = AppInitializerImpl.class.getResourceAsStream("/pdr_configuration.xml"); //async
             OutputStream outputStream = new FileOutputStream(configFile)) {
            int read;
            byte[] bytes = new byte[1024];
            while ((read = inputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, read);
            }
            logger.debug("Successfully loaded default configuration file");
        } catch (IOException e) {
            initExceptions.add(new RuntimeException("Failed to copy default configuration to " + configFile.getAbsolutePath(), e));
        }
    }

    private void downloadVsxDatFile() {
        logger.debug("Starting to download vsx.dat file");
        File tempFile = null;
        try {
            URL url = new URL(vsxDownloadUrl);
            tempFile = new File(FileUtils.getTempDirectory(), "vsx-" + System.currentTimeMillis() + ".dat.gz"); // todo async
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
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            consumer.accept(alert);
        }
    }

    @Override
    public void start() {
        if (!Platform.isFxApplicationThread()) {
            throw new IllegalThreadStateException("This method must be called only from JavaFX Application Thread");
        }
        if (!initializeCalled){
            throw new IllegalStateException("You must call start() after initialize()");
        }
        showInitExceptions();
        showInitErrors();
        showAlerts();
        if(shutdown) try {
            mainApp.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
