package cz.muni.physics;

import com.sun.javafx.application.LauncherImpl;
import cz.muni.physics.plugin.java.JavaPluginLoaderException;
import cz.muni.physics.plugin.java.JavaPluginManager;
import cz.muni.physics.utils.*;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Michal Krajčovič
 * @version 1.0
 * @since 20/03/16
 */
public class MainApp extends Application {

    private final static Logger logger = Logger.getLogger(MainApp.class);

    private final static SpringFxmlLoader loader = new SpringFxmlLoader();

    private static JavaPluginManager javaPluginManager;

    private List<Exception> initExceptions = new ArrayList<>();

    private Stage primaryStage;
    private BorderPane rootLayout;


    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("PDR");

        this.primaryStage.getIcons().add(new Image(MainApp.class.getResourceAsStream("/images/planet.png")));

        initRootLayout();

        showSearch();

        showInitExceptions();


//        showPlugins();
    }

    private void showSearch() {
        AnchorPane searchView = (AnchorPane) loader.load("/view/SearchOverview.fxml");
        rootLayout.setCenter(searchView);
    }

    private void showPlugins() {
        AnchorPane pluginOverview = (AnchorPane) loader.load("/view/PluginsOverview.fxml");
        rootLayout.setCenter(pluginOverview);
    }

    private void initRootLayout() {
        rootLayout = (BorderPane) loader.load("/view/RootLayout.fxml");
        Scene scene = new Scene(rootLayout);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void showInitExceptions() {
        for (Exception initException : initExceptions) {
            FXMLUtil.showExceptionAlert("What a mess!", "Something went wrong during initialization.", initException.getMessage(), initException);
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
        javaPluginManager = ApplicationContextHolder.getBean(JavaPluginManager.class);
        for (File file : javaPluginManager.getPluginJars()) {
            try {
                javaPluginManager.loadPlugin(file);
            } catch (JavaPluginLoaderException e) {
                initExceptions.add(e);
            }
        }
    }

    public static void main(String[] args) {
        LauncherImpl.launchApplication(MainApp.class, MainPreloader.class, args);
    }
}
