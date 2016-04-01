package cz.muni.physics.plugin;

import org.apache.log4j.Logger;

/**
 * @author Michal Krajčovič
 * @version 1.0
 * @since 01/04/16
 */
public class PluginManagerException extends Exception {

    private final static Logger logger = Logger.getLogger(PluginManagerException.class);

    public PluginManagerException(String message) {
        super(message);
        logger.error(message);
    }

    public PluginManagerException(String message, Throwable cause) {
        super(message, cause);
        logger.error(message);
    }
}
