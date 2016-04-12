package cz.muni.physics.pdr.plugin;

import cz.muni.physics.pdr.java.PhotometricData;
import cz.muni.physics.pdr.model.Plugin;
import cz.muni.physics.pdr.utils.AppConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * @author Michal Krajčovič
 * @version 1.0
 * @since 08/04/16
 */
@Component
public class PluginManagerImpl implements PluginManager<PhotometricData> { // TODO make this into prototype, PluginStarter or whatever

    private final static Logger logger = LogManager.getLogger(PluginManagerImpl.class);

    @Autowired
    private AppConfig app;

    public PluginManagerImpl() {
    }

    @Async
    @Override
    public CompletableFuture<List<PhotometricData>> run(Plugin plugin, Map<String, String> params) throws PluginManagerException {
        if (plugin == null) {
            throw new PluginManagerException(""); // todo
        }
        PluginStarter<PhotometricData> starter = new PhotometricDataPluginStarter();
        if (!starter.prepare(plugin.getCommand(), params)) {
            throw new PluginManagerException(""); // todo
        }
        return CompletableFuture.completedFuture(starter.runForResult());
    }

}
