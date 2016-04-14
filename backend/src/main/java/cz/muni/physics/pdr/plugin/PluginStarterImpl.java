package cz.muni.physics.pdr.plugin;

import cz.muni.physics.pdr.entity.PhotometricData;
import cz.muni.physics.pdr.entity.Plugin;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * @author Michal Krajčovič
 * @version 1.0
 * @since 08/04/16
 */
@Component
public class PluginStarterImpl implements PluginStarter<PhotometricData> {

    private final static Logger logger = LogManager.getLogger(PluginStarterImpl.class);

    public PluginStarterImpl() {
    }

    @Async
    @Override
    public CompletableFuture<List<PhotometricData>> run(Plugin plugin, Map<String, String> params) {
        if (plugin == null) {
            throw new IllegalArgumentException("Plugin is null");
        }
        if (params == null) {
            throw new IllegalArgumentException("params cannot be null.");
        }
        ProcessStarter<PhotometricData> starter = new PhotometricDataProcessStarter();
        if (!starter.prepare(plugin.getCommands(), params)) {
            logger.debug("Not able to prepare {} plugin command", plugin.getName());
            return CompletableFuture.completedFuture(Collections.emptyList());
        }
        return CompletableFuture.completedFuture(starter.runForResult());
    }

}
