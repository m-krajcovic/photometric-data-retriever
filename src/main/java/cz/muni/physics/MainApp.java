package cz.muni.physics;

import com.sun.javafx.application.LauncherImpl;
import cz.muni.physics.controller.*;
import cz.muni.physics.java.PhotometricData;
import cz.muni.physics.javafx.PreloaderHandlerEvent;
import cz.muni.physics.model.StarSurvey;
import cz.muni.physics.model.Plugin;
import cz.muni.physics.plugin.PluginManager;
import cz.muni.physics.plugin.PluginManagerException;
import cz.muni.physics.sesame.SesameClient;
import cz.muni.physics.storage.DataStorage;
import cz.muni.physics.utils.ApplicationContextHolder;
import cz.muni.physics.utils.FXMLUtil;
import cz.muni.physics.utils.PropUtils;
import cz.muni.physics.utils.SpringFxmlLoader;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author Michal Krajčovič
 * @version 1.0
 * @since 20/03/16
 */
public class MainApp extends Application {

    private final static Logger logger = LogManager.getLogger(MainApp.class);

    private List<Exception> initExceptions = new ArrayList<>();
    private List<String> initErrors = new ArrayList<>();
    private ObservableList<StarSurvey> starSurveys = FXCollections.observableArrayList(new Callback<StarSurvey, Observable[]>() {
        @Override
        public Observable[] call(StarSurvey param) {
            return new Observable[]{param.nameProperty(), param.sesameAliasProperty(), param.URLProperty(), param.pluginProperty()};
        }
    });
    private ObservableList<Plugin> plugins = FXCollections.observableArrayList();

    private Stage primaryStage;
    private BorderPane rootLayout;

    public MainApp() {
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle(PropUtils.get("app.name"));

        this.primaryStage.getIcons().add(new Image(MainApp.class.getResourceAsStream(PropUtils.get("app.icon"))));

        initRootLayout();

        showSearch();
        showInitExceptions();
        showInitErrors();
    }

    public void showSearch() {
        AnchorPane searchView = (AnchorPane) SpringFxmlLoader.getInstance().load("/view/SearchOverview.fxml");
        ((SearchOverviewController) SpringFxmlLoader.getInstance().getLastLoader().getController()).setMainApp(this);
        rootLayout.setCenter(searchView);
    }

    public void showPhotometricDataOverview(List<PhotometricData> data) {
        AnchorPane photometricDataOverview = (AnchorPane) SpringFxmlLoader.getInstance().load("/view/PhotometricDataOverview.fxml");
        PhotometricDataOverviewController controller = SpringFxmlLoader.getInstance().getLastLoader().getController();
        controller.setMainApp(this);
        controller.setData(data);
        Stage dialogStage = new Stage();
        dialogStage.setTitle("Search result");
        dialogStage.initModality(Modality.WINDOW_MODAL);
        dialogStage.initOwner(primaryStage);
        Scene scene = new Scene(photometricDataOverview);
        dialogStage.setScene(scene);

        dialogStage.show();
    }

    public void showStarSurveyOverview() {
        AnchorPane starSurveyOverview = (AnchorPane) SpringFxmlLoader.getInstance().load("/view/StarSurveyOverview.fxml");
        ((StarSurveyOverviewController) SpringFxmlLoader.getInstance().getLastLoader().getController()).setMainApp(this);
        Stage dialogStage = new Stage();
        dialogStage.setTitle("Star surveys");
        dialogStage.initModality(Modality.WINDOW_MODAL);
        dialogStage.initOwner(primaryStage);
        Scene scene = new Scene(starSurveyOverview);
        dialogStage.setScene(scene);

        dialogStage.show();
    }

    public boolean showStarSurveyEditDialog(StarSurvey record) {
        AnchorPane starSurveyDialog = (AnchorPane) SpringFxmlLoader.getInstance().load("/view/StarSurveyEditDialog.fxml");
        StarSurveyEditDialogController controller = SpringFxmlLoader.getInstance().getLastLoader().getController();
        controller.setMainApp(this);
        controller.setStarSurvey(record);
        Stage dialogStage = new Stage();
        dialogStage.setTitle("Edit Star Survey");
        dialogStage.initModality(Modality.WINDOW_MODAL);
        dialogStage.initOwner(primaryStage);
        Scene scene = new Scene(starSurveyDialog);
        dialogStage.setScene(scene);
        controller.setDialogStage(dialogStage);
        dialogStage.showAndWait();
        return controller.isOkClicked();
    }

    private void initRootLayout() {
        rootLayout = (BorderPane) SpringFxmlLoader.getInstance().load("/view/RootLayout.fxml");
        ApplicationContextHolder.getBean(RootLayoutController.class).setMainApp(this);
        Scene scene = new Scene(rootLayout);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void showInitExceptions() {
        for (Exception initException : initExceptions) {
            FXMLUtil.showExceptionAlert("What a mess!", "Something went wrong during initialization.", initException.getMessage(), initException);
        }
    }

    private void showInitErrors() {
        for (String error : initErrors) {
            FXMLUtil.showAlert("Error", null, error, Alert.AlertType.ERROR);
        }
    }

    @Override
    public void init() throws InterruptedException {
        logger.debug("Initializing application.");
        File dir = DataStorage.getPluginsDir();
        notifyPreloader(PreloaderHandlerEvent.PLUGIN_FOLDER_CHECK);
        if (!dir.exists()) {
            logger.debug("Plugin folder not found, creating new one.");
            dir.mkdir();
        }

        notifyPreloader(PreloaderHandlerEvent.CHECKING_STAR_SURVEYS);
        starSurveys.addListener((ListChangeListener<StarSurvey>) c -> Platform.runLater(() -> DataStorage.saveStarSurveys(new ArrayList<>(c.getList()))));
        starSurveys.addAll(DataStorage.loadStarSurveys()); // TODO catch here

        notifyPreloader(PreloaderHandlerEvent.LOADING_PLUGINS);
        PluginManager pluginManager = ApplicationContextHolder.getBean(PluginManager.class);
        Set<Plugin> availablePlugins = null;
        try {
            availablePlugins = pluginManager.getAvailablePlugins();

        } catch (PluginManagerException e) {
            logger.error(e.getMessage());
            initExceptions.add(e);
        }
        if (availablePlugins == null || availablePlugins.isEmpty()) {
            initErrors.add("There are 0 plugins inside plugins folder.");
        } else {
            for (StarSurvey record : starSurveys) {
                if (!availablePlugins.contains(record.getPlugin())) {
                    initErrors.add(record.getPlugin().getName() + " is not available inside plugins folder.");
                    record.setPlugin(null);
                }
            }
            plugins.addAll(availablePlugins);
        }

        notifyPreloader(PreloaderHandlerEvent.CHECKING_SESAME);
        SesameClient sesameClient = ApplicationContextHolder.getBean(SesameClient.class);
        if (!sesameClient.isAvailable()) {
            logger.error("Sesame Name Resolver is not available right now.");
            initErrors.add("Sesame Name Resolver is not available. Check your internet connection.");
        }
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public ObservableList<Plugin> getPlugins() {
        return plugins;
    }

    public ObservableList<StarSurvey> getStarSurveys() {
        return starSurveys;
    }

    public static void main(String[] args) {
        LauncherImpl.launchApplication(MainApp.class, MainPreloader.class, args);
    }
}
