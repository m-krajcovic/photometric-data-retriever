package cz.muni.physics.pdr.app.updater;

/**
 * @author Michal
 * @version 1.0
 * @since 10/13/2016
 */
public class UpdaterUnavailableException extends Exception {
    public UpdaterUnavailableException(Exception exc) {
        super(exc);
    }
}
