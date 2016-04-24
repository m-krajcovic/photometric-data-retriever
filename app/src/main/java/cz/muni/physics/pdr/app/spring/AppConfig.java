package cz.muni.physics.pdr.app.spring;

import com.thoughtworks.xstream.XStream;
import cz.muni.physics.pdr.app.controller.EntryEditDialogController;
import cz.muni.physics.pdr.app.controller.PatternsOverviewController;
import cz.muni.physics.pdr.app.controller.PhotometricDataOverviewController;
import cz.muni.physics.pdr.app.controller.PreferencesOverviewController;
import cz.muni.physics.pdr.app.controller.StarSurveyEditDialogController;
import cz.muni.physics.pdr.app.controller.StarSurveyOverviewController;
import cz.muni.physics.pdr.app.controller.StellarObjectOverviewController;
import cz.muni.physics.pdr.app.controller.ValuesOverviewController;
import cz.muni.physics.pdr.app.model.PatternModel;
import cz.muni.physics.pdr.app.model.PhotometricDataModel;
import cz.muni.physics.pdr.app.model.StarSurveyModel;
import cz.muni.physics.pdr.app.model.StellarObjectModel;
import cz.muni.physics.pdr.app.model.ValueParameterModel;
import cz.muni.physics.pdr.app.utils.AppInitializer;
import cz.muni.physics.pdr.app.utils.AppInitializerImpl;
import cz.muni.physics.pdr.backend.manager.StarSurveyManager;
import cz.muni.physics.pdr.backend.manager.StarSurveyManagerImpl;
import cz.muni.physics.pdr.backend.repository.config.ConfigurationHolder;
import cz.muni.physics.pdr.backend.repository.config.ConfigurationHolderImpl;
import cz.muni.physics.pdr.backend.repository.plugin.PluginRepository;
import cz.muni.physics.pdr.backend.repository.plugin.PluginRepositoryImpl;
import cz.muni.physics.pdr.backend.repository.starsurvey.StarSurveyRepository;
import cz.muni.physics.pdr.backend.repository.starsurvey.StarSurveyRepositoryConfigImpl;
import cz.muni.physics.pdr.backend.resolver.vsx.VSXStarResolver;
import cz.muni.physics.pdr.backend.resolver.vsx.VSXStarResolverImpl;
import cz.muni.physics.pdr.backend.utils.BackendConfig;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.PreferencesPlaceholderConfigurer;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.prefs.Preferences;

/**
 * @author Michal Krajčovič
 * @version 1.0
 * @since 08/04/16
 */
@Configuration
@Lazy
@ComponentScan(basePackages = {"cz.muni.physics.pdr.app.*"}, lazyInit = true)
@Import(BackendConfig.class)
public class AppConfig {

    private static ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();

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

    public void showPhotometricDataOverview(Map<StarSurveyModel, List<PhotometricDataModel>> data, StellarObjectModel object) {
        SpringFXMLLoader loader = fxmlLoader();
        BorderPane photometricDataOverview = loader.load("/view/PhotometricDataOverview.fxml");
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
        dialogStage.setTitle("Choose one stellar object");
        dialogStage.initOwner(primaryStage);
        dialogStage.initModality(Modality.WINDOW_MODAL);
        Scene scene = new Scene(stellarObjectDialog);
        dialogStage.setScene(scene);
        controller.setDialogStage(dialogStage);
        controller.setItems(stellarObjects);
        dialogStage.showAndWait();
        return controller.getSelected();
    }

    public void showValueParameterOverview(Stage owner) {
        SpringFXMLLoader loader = fxmlLoader();
        AnchorPane dialog = loader.load("/view/ValuesOverview.fxml");
        ValuesOverviewController controller = loader.getController();
        Stage dialogStage = new Stage();
        dialogStage.setTitle("Parameter Values");
        dialogStage.initOwner(owner);
        dialogStage.initModality(Modality.WINDOW_MODAL);
        Scene scene = new Scene(dialog);
        dialogStage.setScene(scene);
        controller.setDialogStage(dialogStage);
        dialogStage.showAndWait();
    }

    public void showPatternsOverview(Stage owner) {
        SpringFXMLLoader loader = fxmlLoader();
        AnchorPane dialog = loader.load("/view/PatternsOverview.fxml");
        PatternsOverviewController controller = loader.getController();
        Stage dialogStage = new Stage();
        dialogStage.setTitle("Patterns");
        dialogStage.initOwner(owner);
        dialogStage.initModality(Modality.WINDOW_MODAL);
        Scene scene = new Scene(dialog);
        dialogStage.setScene(scene);
        controller.setDialogStage(dialogStage);
        dialogStage.showAndWait();
    }

    public boolean showValueParameterEditDialog(ValueParameterModel model, Stage owner) {
        SpringFXMLLoader loader = fxmlLoader();
        AnchorPane valueParameterDialog = loader.load("/view/EntryEditDialog.fxml");
        EntryEditDialogController controller = loader.getController();
        controller.setModel(model);
        Stage dialogStage = new Stage();
        dialogStage.setTitle("Edit Value Parameter");
        dialogStage.initOwner(owner);
        dialogStage.initModality(Modality.WINDOW_MODAL);
        Scene scene = new Scene(valueParameterDialog);
        dialogStage.setScene(scene);
        controller.setDialogStage(dialogStage);
        dialogStage.showAndWait();
        return controller.isOkClicked();
    }

    public boolean showPatternEditDialog(PatternModel model, Stage owner) {
        SpringFXMLLoader loader = fxmlLoader();
        AnchorPane patternsDialog = loader.load("/view/EntryEditDialog.fxml");
        EntryEditDialogController controller = loader.getController();
        controller.setModel(model);
        Stage dialogStage = new Stage();
        dialogStage.setTitle("Edit Pattern");
        dialogStage.initOwner(owner);
        dialogStage.initModality(Modality.WINDOW_MODAL);
        Scene scene = new Scene(patternsDialog);
        dialogStage.setScene(scene);
        controller.setDialogStage(dialogStage);
        dialogStage.showAndWait();
        return controller.isOkClicked();
    }

    @Bean
    public AppInitializer appInitializer(@Value("${app.data.dir.path}") String appDataDirPath,
                                         @Value("${plugins.dir.path}") String pluginDirPath,
                                         @Value("${config.file.name}") String configFileName,
                                         @Value("${vsx.dat.file.name}") String vsxDatFileName,
                                         @Value("${vsx.ftp.url}") String vsxFtpUrl,
                                         @Value("${vsx.check.outdated}") boolean checkOutdated,
                                         Executor executor) {
        AppInitializerImpl initializer = new AppInitializerImpl(new File(appDataDirPath),
                new File(pluginDirPath),
                new File(appDataDirPath, configFileName),
                new File(appDataDirPath, vsxDatFileName),
                vsxFtpUrl,
                checkOutdated);
        initializer.setExecutor(executor);
        return initializer;
    }


    @Bean
    @Scope("prototype")
    public SpringFXMLLoader fxmlLoader() {
        return new SpringFXMLLoader();
    }

    @Bean
    public VSXStarResolver vsxStarResolver(@Value("${app.data.dir.path}") String appDataDirPath,
                                           @Value("${vsx.dat.file.name}") String vsxDatFileName) {
        return new VSXStarResolverImpl(new File(appDataDirPath, vsxDatFileName));
    }

    @Bean
    public StarSurveyManager starSurveyManager(PluginRepository pluginRepository,
                                               StarSurveyRepository starSurveyRepository) {
        return new StarSurveyManagerImpl(pluginRepository, starSurveyRepository);
    }

    @Bean
    public StarSurveyRepository starSurveyRepository(ConfigurationHolder configurationHolder) {
        return new StarSurveyRepositoryConfigImpl(configurationHolder);
    }

    @Bean
    public ConfigurationHolder configurationHolder(XStream xStream,
                                                   @Value("${app.data.dir.path}") String appDataDirPath,
                                                   @Value("${config.file.name}") String configFileName) {
        return new ConfigurationHolderImpl(xStream, new File(appDataDirPath, configFileName));
    }

    @Bean
    public PluginRepository pluginRepository(@Value("${plugins.dir.path}") String pluginDirPath) {
        return new PluginRepositoryImpl(new File(pluginDirPath));
    }

    @Bean
    public ThreadPoolTaskExecutor searchServiceExecutor(@Value("${core.pool.size}") int corePoolSize) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.prefersShortLivedTasks();
        executor.setCorePoolSize(corePoolSize); // min 2 -> max ?
        executor.setDaemon(true);
        executor.setThreadNamePrefix("Backend Thread-");
        return executor;
    }

    @Bean
    public static PropertyPlaceholderConfigurer propertyPlaceholderConfigurer() throws IOException {
        PreferencesPlaceholderConfigurer preferences = new PreferencesPlaceholderConfigurer();
        preferences.setLocations(resourcePatternResolver.getResources("classpath*:application.properties"));
        preferences.setFileEncoding("UTF-8");
        preferences.setUserTreePath(AppConfig.class.getName());
        return preferences;
    }

    @Bean
    public Preferences preferences() {
        return Preferences.userRoot().node(AppConfig.class.getName());
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