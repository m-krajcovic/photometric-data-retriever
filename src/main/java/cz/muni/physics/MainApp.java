package cz.muni.physics;

import com.sun.javafx.application.LauncherImpl;
import cz.muni.physics.utils.AppConfig;
import cz.muni.physics.utils.AppInitializer;
import cz.muni.physics.utils.PropUtils;
import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

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
        LauncherImpl.launchApplication(MainApp.class, MainPreloader.class, args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle(PropUtils.get("app.name"));
        primaryStage.getIcons().add(new Image(MainApp.class.getResourceAsStream(PropUtils.get("app.icon"))));

        app.setPrimaryStage(primaryStage);
        app.initRootLayout();
        app.showSearch();

        initializer.showInitExceptions();
        initializer.showInitErrors();
    }

    @Override
    public void init() throws InterruptedException {
        ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        app = context.getBean(AppConfig.class);
        initializer = context.getBean(AppInitializer.class);

        initializer.initialize(this);
    }
}
