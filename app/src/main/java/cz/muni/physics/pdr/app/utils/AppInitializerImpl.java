package cz.muni.physics.pdr.app.utils;

import cz.muni.physics.pdr.app.javafx.PreloaderHandlerEvent;
import cz.muni.physics.pdr.app.spring.Screens;
import cz.muni.physics.pdr.app.updater.UpdaterService;
import cz.muni.physics.pdr.app.updater.UpdaterStatus;
import cz.muni.physics.pdr.app.updater.UpdaterUnavailableException;
import cz.muni.physics.pdr.backend.resolver.AvailabilityQueryable;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * @author Michal Krajčovič
 * @version 1.0
 * @since 09/04/16
 */
public class AppInitializerImpl implements AppInitializer {
    private static final Logger logger = LogManager.getLogger(AppInitializerImpl.class);

    private File appDataDir;
    private File configFile;


    @Autowired
    private Set<AvailabilityQueryable> services;

    @Autowired
    private UpdaterService updaterService;

    @Autowired
    private Screens app;

    private List<Exception> initExceptions = new ArrayList<>();
    private List<String> initErrors = new ArrayList<>();
    private List<Consumer<Alert>> confirmations = new ArrayList<>();
    private boolean initializeCalled = false;
    private boolean shutdown = false;
    private Executor executor;
    private Stage primaryStage;
    private UpdaterStatus serverStatus;

    public AppInitializerImpl(File appDataDir, File configFile) {
        this.appDataDir = appDataDir;
        this.configFile = configFile;
    }

    @Override
    public void initialize(Application mainApp) {
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
                    logger.warn("Configuration info alert was closed and not confirmed, shutting down app");
                    shutdown = true;
                }
            });
        }

        mainApp.notifyPreloader(PreloaderHandlerEvent.SERVICES_CHECK);
        services.stream().filter(service -> !service.isAvailable()).forEach(service -> {
            String msg = "Service " + service.getServiceName() + " is not available";
            logger.warn(msg);
            initErrors.add(msg);
        });

        mainApp.notifyPreloader(PreloaderHandlerEvent.UPDATE_CHECK);
        try {
            serverStatus = updaterService.status();
        } catch (UpdaterUnavailableException e) {
            String msg = "Updater service is not available right now";
            logger.warn(msg, e);
            initErrors.add(msg);
        }
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

    private void executeTask(Task task) {
        if (executor != null) {
            executor.execute(task);
        } else {
            new Thread(task).start();
        }
    }

    private void showInitExceptions() {
        for (Exception initException : initExceptions) {
            FXMLUtils.showExceptionAlert("What a mess!", "Something went wrong during initialization.", initException.getMessage(), initException, app);
        }
    }

    private void showInitErrors() {
        for (String message : initErrors) {
            FXMLUtils.alert("Error", null, message, Alert.AlertType.ERROR).showAndWait();
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
    public boolean start(Stage primaryStage) {
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
        showUpdater(primaryStage);
        return !shutdown;
    }

    private void showUpdater(Stage primaryStage) {
        if (serverStatus != null && updaterService.getCurrentVersion().compareTo(serverStatus.getServerVersion()) < 0) {
            logger.debug("Initializing update sequence");
            File fileUpdate = null;
            if (serverStatus.getLocalUpdate() != null && serverStatus.getLocalVersion().equals(serverStatus.getServerVersion())) {
                logger.debug("Update is already downloaded.");
                fileUpdate = serverStatus.getLocalUpdate();
            } else {
                logger.debug("Download update");
                Alert alert = FXMLUtils.alert("New update available!", "Do you want to download version " + serverStatus.getServerVersion() + "?"
                        , "Current version is " + updaterService.getCurrentVersion(), Alert.AlertType.CONFIRMATION);
                Optional<ButtonType> buttonResult = alert.showAndWait();
                if (buttonResult.isPresent() && buttonResult.get().equals(ButtonType.OK)) {
                    Task<File> task = new Task<File>() {
                        @Override
                        protected File call() throws Exception {
                            updateMessage("Downloading please wait... (0%)");
                            BiConsumer<Long, Long> consumer = (workDone, max) -> {
                                updateProgress(workDone, max);
                                String percentage = String.format(Locale.ENGLISH, "%.2f", ((double) workDone / max) * 100);
                                updateMessage("Downloading please wait... (" + percentage + "%)");
                            };
                            return updaterService.downloadUpdate(serverStatus, consumer);
                        }
                    };

//                    task.setOnSucceeded(event -> {
//                        Alert alert1 = FXMLUtils.alert("Success!", "Update was downloaded", "", Alert.AlertType.INFORMATION);
//                        alert1.initOwner(primaryStage);
//                        alert1.showAndWait();
//                    });

                    Dialog progressDialog = FXMLUtils.getProgressDialog(primaryStage, task);

                    EventHandler<WorkerStateEvent> errorHandler = event -> {
                        logger.error("Error updating", task.getException());
                        Alert alert1 = FXMLUtils.alert("Error!", "Update has failed", "Try running application as administrator", Alert.AlertType.ERROR);
                        alert.initOwner(primaryStage);
                        alert1.showAndWait();
                        progressDialog.close();
                    };
                    task.setOnFailed(errorHandler);
                    task.setOnCancelled(errorHandler);

                    executor.execute(task);
                    progressDialog.showAndWait();
                    fileUpdate = task.getValue();
                }
            }
            logger.debug("Finished getting update file");
            if (fileUpdate != null) {
                logger.debug("Initializing update application");
                Alert alert = FXMLUtils.alert("Update ready to install!", "Do you want to install update " + serverStatus.getServerVersion() + " now?"
                        , "Current version is " + updaterService.getCurrentVersion(), Alert.AlertType.CONFIRMATION);
                Optional<ButtonType> buttonResult = alert.showAndWait();
                if (buttonResult.isPresent() && buttonResult.get().equals(ButtonType.OK)) {
                    updaterService.applyUpdate(fileUpdate);
                }
            }
        } else if (serverStatus != null && serverStatus.getLocalUpdate() != null) {
            if (serverStatus.getLocalVersion().equals(updaterService.getCurrentVersion())) {
                serverStatus.getLocalUpdate().deleteOnExit();
            }
        }
    }
}
