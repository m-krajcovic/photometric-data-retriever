package cz.muni.physics.pdr.app.spring;

import cz.muni.physics.pdr.app.MainApp;
import cz.muni.physics.pdr.app.controller.StageController;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

/**
 * @author Michal Krajčovič
 * @version 1.0
 * @since 25/04/16
 */
class SpringDialogBuilder {

    private SpringFXMLLoader loader;

    private Stage stage;

    private SpringDialogBuilder(SpringFXMLLoader loader, String resourceUrl) {
        if (loader == null) {
            throw new IllegalArgumentException("loader cannot be null.");
        }
        if (resourceUrl == null) {
            throw new IllegalArgumentException("resourceUrl cannot be null.");
        }
        this.loader = loader;
        Pane pane = loader.load(resourceUrl);
        Scene scene = new Scene(pane);
        stage = new Stage();
        if (loader.getController() instanceof StageController) {
            ((StageController) loader.getController()).setStage(stage);
        }
        stage.setScene(scene);
    }

    static SpringDialogBuilder load(SpringFXMLLoader loader, String resourceUrl) {
        return new SpringDialogBuilder(loader, resourceUrl);
    }

    SpringDialogBuilder stage(String title, Window owner) {
        stage.setTitle(title);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(owner);
        return this;
    }

    SpringDialogBuilder icon(String iconPath) {
        stage.getIcons().add(new Image(MainApp.class.getResourceAsStream(iconPath)));
        return this;
    }

    <T> T controller() {
        return loader.getController();
    }

    Stage get() {
        return stage;
    }
}
