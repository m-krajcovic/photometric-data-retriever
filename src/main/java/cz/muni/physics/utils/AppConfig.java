package cz.muni.physics.utils;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import cz.muni.physics.controller.PhotometricDataOverviewController;
import cz.muni.physics.controller.StarSurveyEditDialogController;
import cz.muni.physics.java.PhotometricData;
import cz.muni.physics.model.Plugin;
import cz.muni.physics.model.StarSurvey;
import cz.muni.physics.nameresolver.NameResolver;
import cz.muni.physics.nameresolver.SesameNameResolver;
import cz.muni.physics.plugin.PluginLoader;
import cz.muni.physics.plugin.PluginLoaderImpl;
import cz.muni.physics.plugin.PluginManager;
import cz.muni.physics.plugin.PluginManagerImpl;
import cz.muni.physics.storage.DataStorage;
import cz.muni.physics.storage.converter.PluginConverter;
import cz.muni.physics.storage.converter.StarSurveyConverter;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;
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
@PropertySource(value = "classpath:application.properties")
public class AppConfig {

    @Autowired
    private Environment environment;

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
    public static PropertyPlaceholderConfigurer propertyPlaceholderConfigurer() {
        return new PropertyPlaceholderConfigurer();
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
    public NameResolver sesameNameResolver() {
        return new SesameNameResolver(new RestTemplate(),
                environment.getProperty("sesame.resolver.url"),
                environment.getProperty("sesame.test.url"));
    }

    @Bean
    public PluginLoader pluginLoader() {
        return new PluginLoaderImpl(System.getProperty("user.home") + environment.getProperty("plugins.dir.path"));
    }

    @Bean
    public PluginManager pluginManager() {
        return new PluginManagerImpl(System.getProperty("user.home") + environment.getProperty("plugins.dir.path"));
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
}
