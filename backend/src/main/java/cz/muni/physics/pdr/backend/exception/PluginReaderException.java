package cz.muni.physics.pdr.backend.exception;

/**
 * @author Michal Krajčovič
 * @version 1.0
 * @since 19/04/16
 */
public class PluginReaderException extends RuntimeException {
    private String pluginDir;

    public PluginReaderException(String message, String pluginDir, Throwable cause) {
        super(message + " " + pluginDir, cause);
        this.pluginDir = pluginDir;
    }

    public PluginReaderException() {
    }

    public PluginReaderException(String message) {
        super(message);
    }

    public PluginReaderException(String message, String pluginDir) {
        super(message + " " + pluginDir);
    }

    public PluginReaderException(String message, Throwable cause) {
        super(message, cause);
    }

    public PluginReaderException(Throwable cause) {
        super(cause);
    }

    public String getPluginDir() {
        return pluginDir;
    }
}
