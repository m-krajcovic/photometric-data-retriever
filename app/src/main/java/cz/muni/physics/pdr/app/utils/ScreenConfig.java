package cz.muni.physics.pdr.app.utils;

import com.thoughtworks.xstream.XStream;
import cz.muni.physics.pdr.app.controller.PhotometricDataOverviewController;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.PreferencesPlaceholderConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.Scope;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

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

    public void showStarSurveyOverview() {
        SpringFXMLLoader loader = fxmlLoader();
        AnchorPane starSurveyOverview = loader.load("/view/StarSurveyOverview.fxml");
        StarSurveyOverviewController controller = loader.getController();
        Stage dialogStage = new Stage();
        dialogStage.setTitle("Star surveys");
        dialogStage.initOwner(primaryStage);
        dialogStage.initModality(Modality.WINDOW_MODAL);
        controller.setPrimaryStage(dialogStage);
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
    @Scope("prototype")
    public SpringFXMLLoader fxmlLoader() {
        return new SpringFXMLLoader();
    }

    @Bean
    public VSXStarResolver vsxStarResolver(@Value("${vsx.dat.file.path:${default.vsx.dat.file.path}}") String vsxDatFilePath) {
        return new VSXStarResolverImpl(vsxDatFilePath);
    }

    @Bean
    public StarSurveyRepository starSurveyRepository(XStream xStream, @Value("${starsurveys.file.path:${default.starsurveys.file.path}}") String starSurveysFilePath) {
        return new StarSurveyRepositoryImpl(xStream, starSurveysFilePath);
    }

    @Bean
    public PluginRepository pluginRepository(@Value("${plugins.dir.path:${default.plugins.dir.path}}") String pluginDirPath) {
        return new PluginRepositoryImpl(pluginDirPath);
    }

    @Bean
    public Executor searchServiceExecutor(@Value("${core.pool.size:${default.core.pool.size}}") int corePoolSize) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.prefersShortLivedTasks();
        System.out.println(corePoolSize);
        executor.setCorePoolSize(corePoolSize); // min 2 -> max ?
        executor.setDaemon(true);
        executor.setThreadNamePrefix("Backend Thread-");
        return executor;
    }

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertyPlaceHolder() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean
    public static PreferencesPlaceholderConfigurer preferencePlaceHolder() {
        PreferencesPlaceholderConfigurer preferencesPlaceholderConfigurer = new PreferencesPlaceholderConfigurer();
        return preferencesPlaceholderConfigurer;
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
