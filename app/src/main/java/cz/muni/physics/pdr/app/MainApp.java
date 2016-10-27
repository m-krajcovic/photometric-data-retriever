package cz.muni.physics.pdr.app;

import com.sun.deploy.uitoolkit.impl.fx.HostServicesFactory;
import cz.muni.physics.pdr.app.spring.AppConfig;
import cz.muni.physics.pdr.app.spring.Screens;
import cz.muni.physics.pdr.app.utils.AppInitializer;
import cz.muni.physics.pdr.app.utils.FXMLUtils;
import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.prefs.BackingStoreException;

/**
 * @author Michal Krajčovič
 * @version 1.0
 * @since 20/03/16
 */
public class MainApp extends Application {

    private static final Logger logger = LogManager.getLogger(MainApp.class);

    private AnnotationConfigApplicationContext context;
    private Screens app;
    private AppInitializer initializer;

    public MainApp() {

    }

    public static void main(String[] args) throws BackingStoreException {
        launch(MainApp.class);
//        LauncherImpl.launchApplication(MainApp.class, MainPreloader.class, args);

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
        app.setHostServices(HostServicesFactory.getInstance(this));
        app.initRootLayout();
        if (initializer.start(primaryStage)) {
            app.showSearch();
        } else {
            stop();
        }
    }

    @Override
    public void init() throws InterruptedException {
        context = new AnnotationConfigApplicationContext(AppConfig.class);
        app = context.getBean(Screens.class);
        initializer = context.getBean(AppInitializer.class);
        initializer.initialize(this);
    }

    @Override
    public void stop() {
        context.close();
        app.getPrimaryStage().close();
    }
}
