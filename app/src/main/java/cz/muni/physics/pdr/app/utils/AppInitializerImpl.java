package cz.muni.physics.pdr.app.utils;

import cz.muni.physics.pdr.app.javafx.PreloaderHandlerEvent;
import cz.muni.physics.pdr.backend.resolver.AvailabilityQueryable;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.function.Consumer;

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

    private Application mainApp;

    @Autowired
    private Set<AvailabilityQueryable> services;

    private List<Exception> initExceptions = new ArrayList<>();
    private List<String> initErrors = new ArrayList<>();
    private List<Consumer<Alert>> confirmations = new ArrayList<>();
    private boolean initializeCalled = false;
    private boolean shutdown = false;
    private Executor executor;
    private Stage primaryStage;

    public AppInitializerImpl(File appDataDir, File pluginsDir, File configFile) {
        this.appDataDir = appDataDir;
        this.pluginsDir = pluginsDir;
        this.configFile = configFile;
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

        mainApp.notifyPreloader(PreloaderHandlerEvent.SERVICES_CHECK);
        services.stream().filter(service -> !service.isAvailable()).forEach(service -> {
            initErrors.add("Service " + service.getServiceName() + " is not available");
        });
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
            FXMLUtils.showExceptionAlert("What a mess!", "Something went wrong during initialization.", initException.getMessage(), initException);
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
