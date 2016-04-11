package cz.muni.physics.pdr.utils;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import cz.muni.physics.pdr.controller.PhotometricDataOverviewController;
import cz.muni.physics.pdr.controller.StarSurveyEditDialogController;
import cz.muni.physics.pdr.java.PhotometricData;
import cz.muni.physics.pdr.model.Plugin;
import cz.muni.physics.pdr.model.StarSurvey;
import cz.muni.physics.pdr.nameresolver.NameResolver;
import cz.muni.physics.pdr.nameresolver.SesameNameResolver;
import cz.muni.physics.pdr.plugin.PluginLoader;
import cz.muni.physics.pdr.plugin.PluginLoaderImpl;
import cz.muni.physics.pdr.plugin.PluginManager;
import cz.muni.physics.pdr.plugin.PluginManagerImpl;
import cz.muni.physics.pdr.storage.DataStorage;
import cz.muni.physics.pdr.storage.converter.PluginConverter;
import cz.muni.physics.pdr.storage.converter.StarSurveyConverter;
import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.Scope;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Michal Krajčovič
 * @version 1.0
 * @since 08/04/16
 */
@Configuration
@Lazy
@ComponentScan(basePackages = {"cz.muni.physics.*"})
@PropertySource("classpath:application.properties")
public class AppConfig {

    @Value("${app.name}")
    private String name;
    @Value("${app.icon.path}")
    private String iconPath;

    private Stage primaryStage;
    private BorderPane rootLayout;
    private ObservableList<StarSurvey> starSurveys;
    private ObservableList<Plugin> plugins;

    public AppConfig() {
        starSurveys = FXCollections.observableArrayList(new Callback<StarSurvey, Observable[]>() {
            @Override
            public Observable[] call(StarSurvey param) {
                return new Observable[]{param.nameProperty(), param.pluginProperty()};
            }
        });
        starSurveys.addListener((ListChangeListener<StarSurvey>) c -> Platform.runLater(() -> dataStorage().saveStarSurveys(new ArrayList<>(c.getList()))));
        plugins = FXCollections.observableArrayList();
    }

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

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
        showScreen(searchView);
    }

    public void showPhotometricDataOverview(Map<StarSurvey, List<PhotometricData>> data) {
        SpringFXMLLoader loader = fxmlLoader();
        AnchorPane photometricDataOverview = loader.load("/view/PhotometricDataOverview.fxml");
        PhotometricDataOverviewController controller = loader.getController();
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
        AnchorPane starSurveyOverview = fxmlLoader().load("/view/StarSurveyOverview.fxml");
        Stage dialogStage = new Stage();
        dialogStage.setTitle("Star surveys");
        dialogStage.initModality(Modality.WINDOW_MODAL);
        dialogStage.initOwner(primaryStage);
        Scene scene = new Scene(starSurveyOverview);
        dialogStage.setScene(scene);
        dialogStage.show();
    }

    public boolean showStarSurveyEditDialog(StarSurvey record) {
        SpringFXMLLoader loader = fxmlLoader();
        AnchorPane starSurveyDialog = loader.load("/view/StarSurveyEditDialog.fxml");
        StarSurveyEditDialogController controller = loader.getController();
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

    public void showScreen(Parent screen) {
        rootLayout.setCenter(screen);
    }

    @Bean
    @Scope("prototype")
    public SpringFXMLLoader fxmlLoader() {
        return new SpringFXMLLoader();
    }

    @Bean
    public DataStorage dataStorage() {
        return new DataStorage();
    }

    @Bean
    public NameResolver sesameNameResolver(@Value("${sesame.resolver.url}") String resolverUrl,
                                           @Value("${sesame.test.url}") String testUrl) {
        return new SesameNameResolver(new RestTemplate(), resolverUrl, testUrl);
    }

    @Bean
    public PluginLoader pluginLoader(@Value("${user.home}${plugins.dir.path}") String pluginsFolderPath) {
        return new PluginLoaderImpl(pluginsFolderPath);
    }

    @Bean
    public PluginManager pluginManager(@Value("${user.home}${plugins.dir.path}") String pluginsFolderPath) {
        return new PluginManagerImpl(pluginsFolderPath);
    }

    @Bean
    public XStream xStream() {
        XStream xStream = new XStream(new DomDriver());
        xStream.alias("starSurvey", StarSurvey.class);
        xStream.alias("plugin", Plugin.class);
        xStream.registerConverter(new PluginConverter());
        xStream.registerConverter(new StarSurveyConverter());
        return xStream;
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public ObservableList<StarSurvey> getStarSurveys() {
        return starSurveys;
    }

    public void setStarSurveys(ObservableList<StarSurvey> starSurveys) {
        this.starSurveys = starSurveys;
    }

    public ObservableList<Plugin> getPlugins() {
        return plugins;
    }

    public void setPlugins(ObservableList<Plugin> plugins) {
        this.plugins = plugins;
    }

    public String getName() {
        return name;
    }

    public String getIconPath() {return iconPath;}
}