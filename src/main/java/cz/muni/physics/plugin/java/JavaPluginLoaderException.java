package cz.muni.physics.plugin.java;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author Michal Krajčovič
 * @version 1.0
 * @since 30/03/16
 */
public class JavaPluginLoaderException extends Exception {

    private final static Logger logger = LogManager.getLogger(JavaPluginLoader.class);

    public JavaPluginLoaderException(String message, Throwable cause) {
        super(message, cause);
        logger.error(message);
    }

    public JavaPluginLoaderException(String message) {
        super(message);
        logger.error(message);
    }
}
