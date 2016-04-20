package cz.muni.physics.pdr.backend.exception;

/**
 * @author Michal Krajčovič
 * @version 1.0
 * @since 19/04/16
 */
public class ResourceAvailabilityException extends RuntimeException {
    private String resourcePath;

    public ResourceAvailabilityException(String message, String resourcePath, Throwable cause) {
        super(message + " " + resourcePath, cause);
        this.resourcePath = resourcePath;
    }

    public String getPath() {
        return resourcePath;
    }

    public void setPath(String path) {
        this.resourcePath = path;
    }
}
