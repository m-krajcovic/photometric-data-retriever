package cz.muni.physics;

import com.sun.javafx.application.LauncherImpl;
import cz.muni.physics.controller.*;
import cz.muni.physics.java.PhotometricData;
import cz.muni.physics.javafx.PreloaderHandlerEvent;
import cz.muni.physics.model.DatabaseRecord;
import cz.muni.physics.model.Plugin;
import cz.muni.physics.plugin.PluginManager;
import cz.muni.physics.plugin.PluginManagerException;
import cz.muni.physics.sesame.SesameClient;
import cz.muni.physics.utils.ApplicationContextHolder;
import cz.muni.physics.utils.FXMLUtil;
import cz.muni.physics.utils.PropUtils;
import cz.muni.physics.utils.SpringFxmlLoader;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Michal Krajčovič
 * @version 1.0
 * @since 20/03/16
 */
public class MainApp extends Application {

    private final static Logger logger = LogManager.getLogger(MainApp.class);

    private List<Exception> initExceptions = new ArrayList<>();
    private List<String> initErrors = new ArrayList<>();
    private ObservableList<DatabaseRecord> dbRecords = FXCollections.observableArrayList();
    private ObservableList<Plugin> plugins = FXCollections.observableArrayList();

    private Stage primaryStage;
    private BorderPane rootLayout;

    public MainApp() {
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("PDR");

        this.primaryStage.getIcons().add(new Image(MainApp.class.getResourceAsStream("/images/planet.png")));

        initRootLayout();

        showSearch();
//        showPlugins();
        showInitExceptions();
        showInitErrors();
    }

    public void showSearch() {
        AnchorPane searchView = (AnchorPane) SpringFxmlLoader.getInstance().load("/view/SearchOverview.fxml");
        ((SearchOverviewController) SpringFxmlLoader.getInstance().getLastLoader().getController()).setMainApp(this);
        rootLayout.setCenter(searchView);
    }

    public void showPhotometricDataOverview(List<PhotometricData> data){
        AnchorPane databaseOverview = (AnchorPane) SpringFxmlLoader.getInstance().load("/view/PhotometricDataOverview.fxml");
        PhotometricDataOverviewController controller = SpringFxmlLoader.getInstance().getLastLoader().getController();
        controller.setMainApp(this);
        controller.setData(data);
        Stage dialogStage = new Stage();
        dialogStage.setTitle("Search result");
        dialogStage.initModality(Modality.WINDOW_MODAL);
        dialogStage.initOwner(primaryStage);
        Scene scene = new Scene(databaseOverview);
        dialogStage.setScene(scene);

        dialogStage.show();
    }

    public void showDatabaseOverview() {
        AnchorPane databaseOverview = (AnchorPane) SpringFxmlLoader.getInstance().load("/view/DatabaseOverview.fxml");
        ((DatabaseOverviewController) SpringFxmlLoader.getInstance().getLastLoader().getController()).setMainApp(this);
        Stage dialogStage = new Stage();
        dialogStage.setTitle("Databases");
        dialogStage.initModality(Modality.WINDOW_MODAL);
        dialogStage.initOwner(primaryStage);
        Scene scene = new Scene(databaseOverview);
        dialogStage.setScene(scene);

        dialogStage.show();
    }

    public boolean showDatabaseEditDialog(DatabaseRecord record) {
        AnchorPane databaseOverview = (AnchorPane) SpringFxmlLoader.getInstance().load("/view/DatabaseEditDialog.fxml");
        DatabaseEditDialogController controller = SpringFxmlLoader.getInstance().getLastLoader().getController();
        controller.setMainApp(this);
        controller.setDbRecord(record);
        Stage dialogStage = new Stage();
        dialogStage.setTitle("Edit DB Record");
        dialogStage.initModality(Modality.WINDOW_MODAL);
        dialogStage.initOwner(primaryStage);
        Scene scene = new Scene(databaseOverview);
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

    private void showInitErrors(){
        for(String error : initErrors){
            System.out.println(error);
            FXMLUtil.showAlert("Error", null, error, Alert.AlertType.ERROR);
        }
    }

    @Override
    public void init() throws InterruptedException {
        logger.debug("Initializing application.");
        File dir = new File(PropUtils.get("plugin.dir.path"));
        notifyPreloader(PreloaderHandlerEvent.PLUGIN_FOLDER_CHECK);
        if (!dir.exists()) {
            logger.debug("Plugin folder not found, creating new one.");
            dir.mkdir();
        }

        notifyPreloader(PreloaderHandlerEvent.LOADING_PLUGINS);
        PluginManager pluginManager = ApplicationContextHolder.getBean(PluginManager.class);

        try {
            plugins.addAll(pluginManager.getAvailablePlugins());
        } catch (PluginManagerException e) {
            initExceptions.add(e);
        }

        notifyPreloader(PreloaderHandlerEvent.CHECKING_DATABASE_RECORDS);
        Plugin nsvsPlugin = plugins.stream().filter(p -> p.getName().equals("NSVS")).findFirst().get();
        Plugin crtsPlugin = plugins.stream().filter(p -> p.getName().equals("CRTS")).findFirst().get();

        dbRecords.add(new DatabaseRecord("NSVS", "http://skydot.lanl.gov/nsvs/star.php?num={id}&mask=32004", nsvsPlugin, "NSVS\\s(?<id>\\d*)"));
        dbRecords.add(new DatabaseRecord("CRTS", "http://nunuku.caltech.edu/cgi-bin/getcssconedb_release_img.cgi?RA={ra}&Dec={dec}&Rad=0.2&DB=photcat&OUT=csv&SHORT=short&PLOT=plot", crtsPlugin, ""));
        //TODO save this somewhere
        notifyPreloader(PreloaderHandlerEvent.CHECKING_SESAME);
        SesameClient sesameClient = ApplicationContextHolder.getBean(SesameClient.class);
        if(!sesameClient.isAvailable()){
            initErrors.add("Sesame Name Resolver is not available. Check your internet connection.");
        }
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public ObservableList<Plugin> getPlugins() {
        return plugins;
    }

    public ObservableList<DatabaseRecord> getDbRecords() {
        return dbRecords;
    }

    public static void main(String[] args) {
        LauncherImpl.launchApplication(MainApp.class, MainPreloader.class, args);
    }
}
