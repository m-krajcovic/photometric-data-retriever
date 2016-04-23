package cz.muni.physics.pdr.backend.repository.config;

import cz.muni.physics.pdr.backend.entity.Configuration;

import java.util.function.Consumer;

/**
 * @author Michal Krajčovič
 * @version 1.0
 * @since 23/04/16
 */
public interface ConfigurationHolder {
    Configuration get();

    void persist(Configuration configuration);

    void addOnConfigurationChange(Consumer<Configuration> consumer);

    void removeOnConfigurationChange(Consumer<Configuration> consumer);
}
