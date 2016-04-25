package cz.muni.physics.pdr.app.utils;

import cz.muni.physics.pdr.app.javafx.PreloaderHandlerEvent;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

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
    private Executor executor;
    private Stage primaryStage;

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
            confirmations.add(alert -> {
                alert.setTitle("Plugins folder not found");
                alert.setHeaderText("Could not find folder " + pluginsDir.getAbsolutePath());
                alert.setContentText("Default plugins will be loaded.");
                Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    loadDefaultPlugins();
                } else {
                    logger.debug("Plugins info alert was closed and not confirmed, shutting down app");
                    shutdown = true;
                }
            });
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
            if (vsxDatFile.lastModified() + 86400000 < System.currentTimeMillis()) {
                logger.debug("File vsx.dat is outdated.");
                confirmations.add(alert -> {
                    alert.setAlertType(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("vsx.dat is outdated.");
                    alert.setHeaderText("File vsx.dat is older than 7 days.");
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

    private void loadDefaultPlugins() {
        Task task = new Task() {
            @Override
            protected Object call() throws Exception {
                logger.debug("Started loading default plugins.");
                try (ZipInputStream zis = new ZipInputStream(AppInitializerImpl.class.getResourceAsStream("/plugins.zip"))) {
                    ZipEntry ze = zis.getNextEntry();
                    byte[] buffer = new byte[1024];
                    while (ze != null) {
                        String filePath = appDataDir + File.separator + ze.getName();
                        if (!ze.isDirectory()) {
                            try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath))) {
                                int read = 0;
                                while ((read = zis.read(buffer)) != -1) {
                                    bos.write(buffer, 0, read);
                                }
                                bos.close();
                            }
                        } else {
                            File dir = new File(filePath);
                            dir.mkdir();
                        }
                        zis.closeEntry();
                        ze = zis.getNextEntry();
                    }
                }
                return null;
            }
        };
        Dialog dialog = FXMLUtils.getProgressDialog(primaryStage, task);
        executeTask(task);
        dialog.showAndWait();
    }

    private void loadDefaultConfigFile() {
        Task task = new Task() {
            @Override
            protected Object call() throws Exception {
                logger.debug("Started loading default config file");
                try (InputStream inputStream = AppInitializerImpl.class.getResourceAsStream("/pdr_configuration.xml");
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
                return null;
            }
        };
        Dialog dialog = FXMLUtils.getProgressDialog(primaryStage, task);
        executeTask(task);
        dialog.showAndWait();
    }

    private void downloadVsxDatFile() {
        Task task = new Task() {
            @Override
            protected Object call() throws Exception {
                logger.debug("Starting to download vsx.dat file");
                try {
                    URL url = new URL(vsxDownloadUrl);
                    long size = 71597;
                    try (GZIPInputStream gzis = new GZIPInputStream(url.openStream());
                         FileOutputStream out = new FileOutputStream(vsxDatFile)) {
                        byte[] buffer = new byte[1024];
                        long i = 0;
                        int read;
                        while ((read = gzis.read(buffer)) > 0) {
                            out.write(buffer, 0, read);
                            i++;
                            updateProgress(i, size);
                            double p = (i / (double) size);
                            if (p > 1) p = 1;
                            updateMessage(String.format("%.1f", 100 * p) + "%");
                        }
                        logger.debug("Successfully downloaded vsx.dat file");
                    }
                } catch (IOException e) {
                    logger.error("Failed to download vsx.dat file", e);
                    initErrors.add("Failed to download vsx.dat file. Try downloading it manually and put it into pdr app data folder");
                }
                return null;
            }
        };
        Dialog dialog = FXMLUtils.getProgressDialog(primaryStage, task);
        executeTask(task);
        dialog.showAndWait();
    }

    private void executeTask(Task task) {
        if (executor != null) {
            executor.execute(task);
        } else {
            new Thread(task).start();
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
            alert.initOwner(primaryStage);
            consumer.accept(alert);
        }
    }

    public void setExecutor(Executor executor) {
        this.executor = executor;
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        if (!Platform.isFxApplicationThread()) {
            throw new IllegalThreadStateException("This method must be called only from JavaFX Application Thread");
        }
        if (!initializeCalled) {
            throw new IllegalStateException("You must call start() after initialize()");
        }
        showInitExceptions();
        showInitErrors();
        showAlerts();
        if (shutdown)
            primaryStage.close();
    }
}
