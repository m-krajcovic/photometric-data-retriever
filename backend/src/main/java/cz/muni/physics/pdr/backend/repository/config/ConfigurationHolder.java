package cz.muni.physics.pdr.backend.repository.config;

import cz.muni.physics.pdr.backend.entity.Configuration;

import java.util.function.Consumer;

/**
 * Class for controlling configuration file
 * @author Michal Krajčovič
 * @version 1.0
 * @since 23/04/16
 */
public interface ConfigurationHolder {
    /**
     * This method returns Configuration object with information from configuration file
     * @return
     */
    Configuration get();

    /**
     * This method updates configuration file
     * @param configuration
     */
    void persist(Configuration configuration);

    /**
     * Adds consumer on configuration change. Consumer calling is LAZY. That means consumer will be called only on calling get()
     * method and configuration file has been changed since last time
     * @param consumer Consumer to call on change
     */
    void addOnConfigurationChange(Consumer<Configuration> consumer);

    /**
     * Removes consumer from configuration change
     * @param consumer Consumer to remove
     */
    void removeOnConfigurationChange(Consumer<Configuration> consumer);
}
