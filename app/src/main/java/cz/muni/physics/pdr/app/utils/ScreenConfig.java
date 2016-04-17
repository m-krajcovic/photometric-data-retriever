package cz.muni.physics.pdr.app.utils;

import cz.muni.physics.pdr.app.controller.PhotometricDataOverviewController;
import cz.muni.physics.pdr.app.controller.StarSurveyEditDialogController;
import cz.muni.physics.pdr.app.controller.StellarObjectOverviewController;
import cz.muni.physics.pdr.app.model.PhotometricDataModel;
import cz.muni.physics.pdr.app.model.StarSurveyModel;
import cz.muni.physics.pdr.app.model.StellarObjectModel;
import cz.muni.physics.pdr.backend.entity.StarSurvey;
import cz.muni.physics.pdr.backend.entity.StellarObject;
import cz.muni.physics.pdr.backend.utils.AppConfig;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.Scope;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

import java.util.List;
import java.util.Map;

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

    @Autowired
    private AppConfig backend;

    @Value("${app.name}")
    private String name;
    @Value("${app.icon.path}")
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
        dialogStage.setTitle("Search result");
        dialogStage.initModality(Modality.WINDOW_MODAL);
        dialogStage.initOwner(primaryStage);
        Scene scene = new Scene(photometricDataOverview);
        dialogStage.setScene(scene);
        dialogStage.show();
        controller.setStellarObject(object);
        controller.setData(data);
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

    public boolean showStarSurveyEditDialog(StarSurveyModel record) {
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

    public StellarObjectModel showStellarObjects(List<StellarObjectModel> stellarObjects, EventHandler<WindowEvent> onClose) {
        SpringFXMLLoader loader = fxmlLoader();
        AnchorPane stellarObjectDialog = loader.load("/view/StellarObjectOverview.fxml");
        StellarObjectOverviewController controller = loader.getController();
        Stage dialogStage = new Stage();
        dialogStage.setTitle("Edit Star Survey");
        dialogStage.initModality(Modality.WINDOW_MODAL);
        dialogStage.initOwner(primaryStage);
        Scene scene = new Scene(stellarObjectDialog);
        dialogStage.setScene(scene);
        controller.setDialogStage(dialogStage);
        controller.setItems(stellarObjects);
        dialogStage.setOnCloseRequest(onClose);
        dialogStage.showAndWait();
        return controller.getSelected();
    }

    @Bean
    @Scope("prototype")
    public SpringFXMLLoader fxmlLoader() {
        return new SpringFXMLLoader();
    }

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
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