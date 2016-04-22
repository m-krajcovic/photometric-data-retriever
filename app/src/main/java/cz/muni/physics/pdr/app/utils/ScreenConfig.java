package cz.muni.physics.pdr.app.utils;

import com.thoughtworks.xstream.XStream;
import cz.muni.physics.pdr.app.controller.PhotometricDataOverviewController;
import cz.muni.physics.pdr.app.controller.PreferencesOverviewController;
import cz.muni.physics.pdr.app.controller.StarSurveyEditDialogController;
import cz.muni.physics.pdr.app.controller.StarSurveyOverviewController;
import cz.muni.physics.pdr.app.controller.StellarObjectOverviewController;
import cz.muni.physics.pdr.app.model.PhotometricDataModel;
import cz.muni.physics.pdr.app.model.StarSurveyModel;
import cz.muni.physics.pdr.app.model.StellarObjectModel;
import cz.muni.physics.pdr.backend.entity.StarSurvey;
import cz.muni.physics.pdr.backend.entity.StellarObject;
import cz.muni.physics.pdr.backend.repository.plugin.PluginRepository;
import cz.muni.physics.pdr.backend.repository.plugin.PluginRepositoryImpl;
import cz.muni.physics.pdr.backend.repository.starsurvey.StarSurveyRepository;
import cz.muni.physics.pdr.backend.repository.starsurvey.StarSurveyRepositoryImpl;
import cz.muni.physics.pdr.backend.resolver.vsx.VSXStarResolver;
import cz.muni.physics.pdr.backend.resolver.vsx.VSXStarResolverImpl;
import cz.muni.physics.pdr.backend.utils.AppConfig;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.Scope;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;

/**
 * @author Michal Krajčovič
 * @version 1.0
 * @since 08/04/16
 */
@Configuration
@ComponentScan(basePackages = {"cz.muni.physics.pdr.app.*"})
@PropertySource("classpath:application.properties")
@Import(AppConfig.class)
public class ScreenConfig {

    @Value("${app.name:PDR}")
    private String name;
    @Value("${app.icon.path:/images/planet.png}")
    private String iconPath;
    @Autowired
    private Environment environment;

    private Stage primaryStage;
    private BorderPane rootLayout;

    public void initRootLayout() {
        SpringFXMLLoader loader = fxmlLoader();
        rootLayout = loader.load("/view/RootLayout.fxml");
        Scene scene = new Scene(rootLayout);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void showSearch() {
        SpringFXMLLoader loader = fxmlLoader();
        AnchorPane searchView = loader.load("/view/SearchOverview.fxml");
        rootLayout.setCenter(searchView);
    }

    public void showPhotometricDataOverview(Map<StarSurvey, List<PhotometricDataModel>> data, StellarObject object) {
        SpringFXMLLoader loader = fxmlLoader();
        AnchorPane photometricDataOverview = loader.load("/view/PhotometricDataOverview.fxml");
        PhotometricDataOverviewController controller = loader.getController();
        Stage dialogStage = new Stage();
        dialogStage.setTitle("Search stellarObject");
        dialogStage.initModality(Modality.WINDOW_MODAL);
        dialogStage.initOwner(primaryStage);
        Scene scene = new Scene(photometricDataOverview);
        dialogStage.setScene(scene);
        dialogStage.show();
        controller.setStellarObject(object);
        controller.setData(data);
    }

    public void showPreferencesOverview() {
        SpringFXMLLoader loader = fxmlLoader();
        AnchorPane starSurveyOverview = loader.load("/view/PreferencesOverview.fxml");
        PreferencesOverviewController controller = loader.getController();
        Stage dialogStage = new Stage();
        dialogStage.setTitle("Star surveys");
        dialogStage.initOwner(primaryStage);
        dialogStage.initModality(Modality.WINDOW_MODAL);
        controller.setDialogStage(dialogStage);
        Scene scene = new Scene(starSurveyOverview);
        dialogStage.setScene(scene);
        dialogStage.show();
    }

    public void showStarSurveyOverview() {
        SpringFXMLLoader loader = fxmlLoader();
        AnchorPane starSurveyOverview = loader.load("/view/StarSurveyOverview.fxml");
        StarSurveyOverviewController controller = loader.getController();
        Stage dialogStage = new Stage();
        dialogStage.setTitle("Star surveys");
        dialogStage.initOwner(primaryStage);
        dialogStage.initModality(Modality.WINDOW_MODAL);
        controller.setDialogStage(dialogStage);
        Scene scene = new Scene(starSurveyOverview);
        dialogStage.setScene(scene);
        dialogStage.show();
    }

    public boolean showStarSurveyEditDialog(StarSurveyModel record, Window owner) {
        SpringFXMLLoader loader = fxmlLoader();
        AnchorPane starSurveyDialog = loader.load("/view/StarSurveyEditDialog.fxml");
        StarSurveyEditDialogController controller = loader.getController();
        controller.setStarSurvey(record);
        Stage dialogStage = new Stage();
        dialogStage.setTitle("Edit Star Survey");
        dialogStage.initOwner(owner);
        dialogStage.initModality(Modality.WINDOW_MODAL);
        Scene scene = new Scene(starSurveyDialog);
        dialogStage.setScene(scene);
        controller.setDialogStage(dialogStage);
        dialogStage.showAndWait();
        return controller.isOkClicked();
    }

    public StellarObjectModel showStellarObjects(List<StellarObjectModel> stellarObjects) {
        SpringFXMLLoader loader = fxmlLoader();
        AnchorPane stellarObjectDialog = loader.load("/view/StellarObjectOverview.fxml");
        StellarObjectOverviewController controller = loader.getController();
        Stage dialogStage = new Stage();
        dialogStage.setTitle("Edit Star Survey");
        dialogStage.initOwner(primaryStage);
        dialogStage.initModality(Modality.WINDOW_MODAL);
        Scene scene = new Scene(stellarObjectDialog);
        dialogStage.setScene(scene);
        controller.setDialogStage(dialogStage);
        controller.setItems(stellarObjects);
        dialogStage.showAndWait();
        return controller.getSelected();
    }

    @Bean
    public AppInitializer appInitializer(@Value("#{preferences.get('app.data.dir.path')}") String appDataDirPath,
                                         @Value("#{preferences.get('plugins.dir.path')}") String pluginDirPath,
                                         @Value("${starsurveys.file.name}") String starSurveysFileName,
                                         @Value("${vsx.dat.file.name}") String vsxDatFileName) {
        return new AppInitializer(new File(appDataDirPath),
                new File(pluginDirPath),
                new File(appDataDirPath, starSurveysFileName),
                new File(appDataDirPath, vsxDatFileName));
    }


    @Bean
    @Scope("prototype")
    public SpringFXMLLoader fxmlLoader() {
        return new SpringFXMLLoader();
    }

    @Bean
    public VSXStarResolver vsxStarResolver(@Value("#{preferences.get('app.data.dir.path')}") String appDataDirPath,
                                           @Value("${vsx.dat.file.name}") String vsxDatFileName) {
        return new VSXStarResolverImpl(new File(appDataDirPath, vsxDatFileName));
    }

    @Bean
    public StarSurveyRepository starSurveyRepository(XStream xStream,
                                                     @Value("#{preferences.get('app.data.dir.path')}") String appDataDirPath,
                                                     @Value("${starsurveys.file.name}") String starSurveysFileName) {
        return new StarSurveyRepositoryImpl(xStream, new File(appDataDirPath, starSurveysFileName));
    }

    @Bean
    public PluginRepository pluginRepository(@Value("#{preferences.get('plugins.dir.path')}") String pluginDirPath) {
        return new PluginRepositoryImpl(new File(pluginDirPath));
    }

    @Bean
    public Executor searchServiceExecutor(@Value("#{preferences.get('core.pool.size')}") int corePoolSize) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.prefersShortLivedTasks();
        executor.setCorePoolSize(corePoolSize); // min 2 -> max ?
        executor.setDaemon(true);
        executor.setThreadNamePrefix("Backend Thread-");
        return executor;
    }

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertyConfig() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean
    public PreferencesHolder preferences() {
        return new PreferencesHolder();
    }


    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public String getName() {
        return name;
    }

    public String getIconPath() {
        return iconPath;
    }
}
