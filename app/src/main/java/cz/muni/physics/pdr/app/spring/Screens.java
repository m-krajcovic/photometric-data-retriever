package cz.muni.physics.pdr.app.spring;

import cz.muni.physics.pdr.app.controller.EntryEditDialogController;
import cz.muni.physics.pdr.app.controller.PhotometricDataOverviewController;
import cz.muni.physics.pdr.app.controller.SearchReportDialogController;
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
//    private HostServicesDelegate hostServices;


    public void initRootLayout() {
        SpringFXMLLoader loader = fxmlLoader();
        rootLayout = loader.load("/view/RootLayout.fxml");
        Scene scene = new Scene(rootLayout);
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
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
                        .stage(resources.getString("search.result")).resizable();
        PhotometricDataOverviewController controller = builder.controller();
        Stage stage = builder.get();
        controller.setStellarObject(object);
        controller.setData(data);
        stage.show();
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

    public StellarObjectModel showStellarObjects(Map<String, List<StellarObjectModel>> models) {
        SpringDialogBuilder builder =
                SpringDialogBuilder.load(fxmlLoader(), "/view/StellarObjectOverview.fxml")
                        .stage(resources.getString("choose.one.stellar.object"), primaryStage);
        StellarObjectOverviewController controller = builder.controller();
        controller.setItems(models);
        Stage stage = builder.get();
//        stage.setResizable(true);
        stage.showAndWait();
        return controller.getSelected();
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

    public void showSearchReport(Stage owner, StellarObjectModel stellarObject, Map<StarSurveyModel, List<PhotometricDataModel>> data) {
        SpringDialogBuilder builder =
                SpringDialogBuilder.load(fxmlLoader(), "/view/SearchReportDialog.fxml")
                        .stage(resources.getString("search.report"), owner);
        SearchReportDialogController controller = builder.controller();
        controller.setData(stellarObject, data);
        builder.get().showAndWait();
    }

    public void showAboutOverview(Stage owner) {
        SpringDialogBuilder.load(fxmlLoader(), "/view/AboutOverview.fxml")
                .stage(resources.getString("stage.about"), owner).get().showAndWait();
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

    public void showReportErrorWindow(Stage owner) {
            SpringDialogBuilder builder =
                    SpringDialogBuilder.load(fxmlLoader(), "/view/ReportErrorDialog.fxml")
                            .stage(resources.getString("report.error"), owner);
            builder.get().showAndWait();
        }
}
