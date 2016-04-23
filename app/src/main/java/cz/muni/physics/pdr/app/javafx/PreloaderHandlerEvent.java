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
    DATA_DIR_CHECK("Searching for application data folder..."),
    CONFIG_FILE_CHECK("Searching for configuration file...");

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
