package cz.muni.physics.pdr.app;

import com.sun.javafx.application.LauncherImpl;
import cz.muni.physics.pdr.app.spring.AppConfig;
import cz.muni.physics.pdr.app.utils.AppInitializer;
import cz.muni.physics.pdr.app.utils.FXMLUtils;
import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

/**
 * @author Michal Krajčovič
 * @version 1.0
 * @since 20/03/16
 */
public class MainApp extends Application {

    private final static Logger logger = LogManager.getLogger(MainApp.class);

    private AppConfig app;
    private AppInitializer initializer;

    public MainApp() {

    }

    public static void main(String[] args) {
        try {
            Preferences.userRoot().clear();
        } catch (BackingStoreException e) {
            e.printStackTrace();
        }
//        launch(MainApp.class);
        LauncherImpl.launchApplication(MainApp.class, MainPreloader.class, args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Thread.currentThread().setUncaughtExceptionHandler((t, e) -> {
            logger.error(e.getMessage(), e);
            FXMLUtils.showExceptionAlert("Something went terribly wrong!", "Your best bet is to restart this application", e.getMessage(), e);
        });
        primaryStage.setTitle(app.getName());
        primaryStage.getIcons().add(new Image(MainApp.class.getResourceAsStream(app.getIconPath())));
        app.setPrimaryStage(primaryStage);
        app.initRootLayout();
        initializer.start(primaryStage);
        app.showSearch();

    }

    @Override
    public void init() throws InterruptedException {
        ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        app = context.getBean(AppConfig.class);
        initializer = context.getBean(AppInitializer.class);
        initializer.initialize(this);
    }

    @Override
    public void stop() {
        app.getPrimaryStage().close();
    }
}
