package cz.muni.physics.pdr.app.javafx;

import javafx.application.Preloader;

/**
 * @author Michal Krajčovič
 * @version 1.0
 * @since 31/03/16
 */
public enum PreloaderHandlerEvent implements Preloader.PreloaderNotification {

    PLUGIN_FOLDER_CHECK("Searching for plugins folder..."),
    VSX_DAT_CHECK("Searching for vsx.dat file..."),
    STAR_SURVEYS_CHECK("Searching for star surveys config..."),
    DATA_DIR_CHECK("Searching for application data folder...");

    private String message;

    PreloaderHandlerEvent(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
