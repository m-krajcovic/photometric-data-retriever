package cz.muni.physics.pdr.javafx;

import javafx.application.Preloader;

/**
 * @author Michal Krajčovič
 * @version 1.0
 * @since 31/03/16
 */
public class PreloaderHandlerEvent implements Preloader.PreloaderNotification {

    public static final PreloaderHandlerEvent PLUGIN_FOLDER_CHECK = new PreloaderHandlerEvent("Looking for plugins folder...");
    public static final PreloaderHandlerEvent LOADING_PLUGINS = new PreloaderHandlerEvent("Loading plugins...");
    public static final PreloaderHandlerEvent CHECKING_SESAME = new PreloaderHandlerEvent("Checking Sesame availability...");
    public static final PreloaderHandlerEvent CHECKING_STAR_SURVEYS = new PreloaderHandlerEvent("Looking for database records...");

    private String message;

    private PreloaderHandlerEvent(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
