package cz.muni.physics.pdr.backend.resolver;

/**
 * @author Michal
 * @version 1.0
 * @since 8/2/2016
 */
public interface AvailabilityQueryable {
    /**
     * Checks if Service is available
     * @return
     */
    boolean isAvailable();

    String getServiceName();
}
