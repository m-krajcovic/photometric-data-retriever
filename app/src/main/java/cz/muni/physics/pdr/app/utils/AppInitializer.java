package cz.muni.physics.pdr.app.utils;

import javafx.application.Application;

/**
 * This class is used to put initialization of javafx.application.Application to one place,
 * which helps with readability of Application implementation
 *
 * @author Michal Krajčovič
 * @version 1.0
 * @see javafx.application.Application
 * @since 23/04/16
 */
public interface AppInitializer {

    /**
     * This method should be called during init phase of Application and is completed during Preloader
     *
     * @param mainApp Application class you are calling this from e.g. intializer.initialize(this). This is needed to send notifications to Preloader
     * @see javafx.application.Application
     * @see javafx.application.Preloader
     */
    void initialize(Application mainApp);

    /**
     * This method should be called ONLY in JavaFX Application Thread (Preferably from Application.start(Stage stage) method)
     *
     * @see javafx.application.Application
     */
    void start();
}
