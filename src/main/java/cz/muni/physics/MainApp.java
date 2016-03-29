package cz.muni.physics;

import cz.muni.physics.plugin.java.JavaPluginManager;
import cz.muni.physics.utils.ApplicationContextHolder;
import cz.muni.physics.utils.PropUtils;
import cz.muni.physics.utils.SpringFxmlLoader;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.apache.log4j.Logger;

import java.io.File;

/**
 * @author Michal Krajčovič
 * @version 1.0
 * @since 20/03/16
 */
public class MainApp extends Application {

    private final static Logger logger = Logger.getLogger(MainApp.class);

    private final static SpringFxmlLoader loader = new SpringFxmlLoader();

    private static JavaPluginManager javaPluginManager;

    private Stage primaryStage;
    private BorderPane rootLayout;

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("PDR");

        this.primaryStage.getIcons().add(new Image(MainApp.class.getResourceAsStream("/images/planet.png")));

        initRootLayout();

        showSearch();
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

    @Override
    public void init() throws Exception {
        File dir = new File(PropUtils.get("plugin.dir.path"));
        if(!dir.exists()){
            logger.debug("Plugin folder not found, creating new one.");
            dir.mkdir();
        }
        javaPluginManager = ApplicationContextHolder.getBean(JavaPluginManager.class);
        javaPluginManager.loadAllPlugins();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
