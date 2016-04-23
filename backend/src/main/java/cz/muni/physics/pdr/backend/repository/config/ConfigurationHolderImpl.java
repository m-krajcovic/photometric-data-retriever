package cz.muni.physics.pdr.backend.repository.config;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.XStreamException;
import cz.muni.physics.pdr.backend.entity.Configuration;
import cz.muni.physics.pdr.backend.exception.ResourceAvailabilityException;
import cz.muni.physics.pdr.backend.repository.FileWatcher;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

/**
 * @author Michal Krajčovič
 * @version 1.0
 * @since 23/04/16
 */
public class ConfigurationHolderImpl implements ConfigurationHolder {
    private final static Logger logger = LogManager.getLogger(ConfigurationHolderImpl.class);

    private File configurationFile;
    private XStream xStream;
    private Configuration configuration;
    private FileWatcher fileWatcher;
    private Set<Consumer<Configuration>> onConfigurationChange = new HashSet<>();

    public ConfigurationHolderImpl(XStream xStream,
                                   File configurationFile) {
        this.xStream = xStream;
        this.configurationFile = configurationFile;
        this.fileWatcher = new FileWatcher(configurationFile);
    }

    @Override
    public Configuration get() {
        checkAndReload();
        return configuration;
    }

    @Override
    public void persist(Configuration configuration) {
        this.configuration = configuration;
        try (Writer writer = new FileWriter(configurationFile)) {
            xStream.toXML(configuration, writer);
            onConfigurationChange.forEach(consumer -> consumer.accept(configuration));
        } catch (IOException e) {
            throw new ResourceAvailabilityException("Failed to save to file", configurationFile.getAbsolutePath(), e);
        }
    }

    @Override
    public void addOnConfigurationChange(Consumer<Configuration> consumer) {
        onConfigurationChange.add(consumer);
    }

    @Override
    public void removeOnConfigurationChange(Consumer<Configuration> consumer) {
        onConfigurationChange.remove(consumer);
    }

    private void checkAndReload() {
        if (configuration == null) {
            logger.debug("Configuration file was not loaded yet. Loading now.");
            this.configuration = loadConfig();
        } else if (fileWatcher.isFileUpdated()) {
            logger.debug("Configuration file has change. Reloading now.");
            this.configuration = loadConfig();
            onConfigurationChange.forEach(consumer -> consumer.accept(configuration));
        }
    }

    private synchronized Configuration loadConfig(int retryCount) {
        try (Reader reader = new FileReader(configurationFile)) {
            return (Configuration) xStream.fromXML(reader);
        } catch (IOException | XStreamException e) {
            if (this.configuration != null && retryCount <= 3) {
                persist(this.configuration);
                return loadConfig(++retryCount);
            } else {
                throw new ResourceAvailabilityException("Failed to load file", configurationFile.getAbsolutePath(), e);
            }
        }
    }

    private synchronized Configuration loadConfig() {
        return loadConfig(0);
    }
}
