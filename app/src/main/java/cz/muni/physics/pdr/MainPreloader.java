package cz.muni.physics.pdr;

import cz.muni.physics.pdr.javafx.PreloaderHandlerEvent;
import javafx.application.Preloader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * @author Michal Krajčovič
 * @version 1.0
 * @since 20/03/16
 */
public class MainPreloader extends Preloader {

    private Stage preloaderStage;
    private Label infoLabel;

    @Override
    public void start(Stage primaryStage) throws Exception {
        preloaderStage = primaryStage;
        VBox loading = new VBox(20);
        loading.setMaxWidth(Region.USE_PREF_SIZE);
        loading.setMaxHeight(Region.USE_PREF_SIZE);
        ProgressBar progressBar = new ProgressBar();
        progressBar.setPrefWidth(240);
        loading.getChildren().add(progressBar);
        infoLabel = new Label("Initializing application...");
        loading.getChildren().add(infoLabel);
        BorderPane root = new BorderPane(loading);
        Scene scene = new Scene(root);
        primaryStage.setWidth(300);
        primaryStage.setHeight(150);
        primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    @Override
    public void handleStateChangeNotification(StateChangeNotification stateChangeNotification) {
        if (stateChangeNotification.getType() == StateChangeNotification.Type.BEFORE_START) {
            preloaderStage.hide();
        }
    }

    @Override
    public void handleApplicationNotification(PreloaderNotification info) {
        if (info instanceof PreloaderHandlerEvent) {
            infoLabel.setText(((PreloaderHandlerEvent) info).getMessage());
        }
    }
}
