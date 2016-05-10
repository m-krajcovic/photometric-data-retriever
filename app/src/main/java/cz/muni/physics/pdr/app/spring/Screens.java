package cz.muni.physics.pdr.app.spring;

import cz.muni.physics.pdr.app.controller.EntryEditDialogController;
import cz.muni.physics.pdr.app.controller.PhotometricDataOverviewController;
import cz.muni.physics.pdr.app.controller.StarSurveyEditDialogController;
import cz.muni.physics.pdr.app.controller.StellarObjectOverviewController;
import cz.muni.physics.pdr.app.model.EntryModel;
import cz.muni.physics.pdr.app.model.PhotometricDataModel;
import cz.muni.physics.pdr.app.model.StarSurveyModel;
import cz.muni.physics.pdr.app.model.StellarObjectModel;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.function.BiPredicate;

/**
 * @author Michal Krajčovič
 * @version 1.0
 * @since 25/04/16
 */
@Component
public class Screens {

    @Autowired
    private ApplicationContext ctx;
    @Autowired
    private ResourceBundle resources;

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
        Pane searchView = loader.load("/view/SearchOverview.fxml");
        rootLayout.setCenter(searchView);
    }

    public void showPhotometricDataOverview(Map<StarSurveyModel, List<PhotometricDataModel>> data, StellarObjectModel object) {
        SpringDialogBuilder builder =
                SpringDialogBuilder.load(fxmlLoader(), "/view/PhotometricDataOverview.fxml")
                        .stage(resources.getString("search.result"), primaryStage);
        PhotometricDataOverviewController controller = builder.controller();
        Stage stage = builder.get();
        controller.setStellarObject(object);
        controller.setData(data);
        stage.showAndWait();
    }

    public void showPreferencesOverview() {
        SpringDialogBuilder.load(fxmlLoader(), "/view/PreferencesOverview.fxml")
                .stage(resources.getString("preferences"), primaryStage).get().showAndWait();
    }

    public void showStarSurveyOverview() {
        SpringDialogBuilder.load(fxmlLoader(), "/view/StarSurveyOverview.fxml")
                .stage("Star surveys", primaryStage)
                .get().showAndWait();
    }

    public boolean showStarSurveyEditDialog(StarSurveyModel record, Window owner) {
        SpringDialogBuilder builder =
                SpringDialogBuilder.load(fxmlLoader(), "/view/StarSurveyEditDialog.fxml")
                        .stage(resources.getString("edit.star.survey"), owner);
        StarSurveyEditDialogController controller = builder.controller();
        controller.setStarSurvey(record);
        Stage stage = builder.get();
        stage.showAndWait();
        return controller.isOkClicked();
    }

    public StellarObjectModel showStellarObjects(List<StellarObjectModel> stellarObjects) {
        SpringDialogBuilder builder =
                SpringDialogBuilder.load(fxmlLoader(), "/view/StellarObjectOverview.fxml")
                        .stage(resources.getString("choose.one.stellar.object"), primaryStage);
        StellarObjectOverviewController controller = builder.controller();
        controller.setItems(stellarObjects);
        Stage stage = builder.get();
        stage.showAndWait();
        return controller.getSelected();
    }

    public void showValueParameterOverview(Stage owner) {
        SpringDialogBuilder.load(fxmlLoader(), "/view/ValuesOverview.fxml")
                .stage(resources.getString("values"), owner)
                .get().showAndWait();
    }

    public void showPatternsOverview(Stage owner) {
        SpringDialogBuilder.load(fxmlLoader(), "/view/PatternsOverview.fxml")
                .stage(resources.getString("patterns"), owner)
                .get().showAndWait();
    }

    public boolean showEntryEditDialog(EntryModel model, Stage owner) {
        return showEntryEditDialog(model, owner, null);
    }

    public boolean showEntryEditDialog(EntryModel model, Stage owner, BiPredicate<TextField, TextField> validCheck) {
        SpringDialogBuilder builder =
                SpringDialogBuilder.load(fxmlLoader(), "/view/EntryEditDialog.fxml")
                        .stage(resources.getString("edit.entry"), owner);
        EntryEditDialogController controller = builder.controller();
        controller.setValidCheck(validCheck);
        controller.setModel(model);
        Stage stage = builder.get();
        stage.showAndWait();
        return controller.isOkClicked();
    }

    private SpringFXMLLoader fxmlLoader() {
        return ctx.getBean(SpringFXMLLoader.class);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIconPath() {
        return iconPath;
    }

    public void setIconPath(String iconPath) {
        this.iconPath = iconPath;
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public BorderPane getRootLayout() {
        return rootLayout;
    }

    public void setRootLayout(BorderPane rootLayout) {
        this.rootLayout = rootLayout;
    }
}
