package cz.muni.physics.plugin;


/**
 * @author Michal Krajčovič
 * @version 1.0
 * @since 01/04/16
 */
public class PluginManagerException extends Exception {

    public PluginManagerException(String message) {
        super(message);
    }

    public PluginManagerException(String message, Throwable cause) {
        super(message, cause);
    }
}
