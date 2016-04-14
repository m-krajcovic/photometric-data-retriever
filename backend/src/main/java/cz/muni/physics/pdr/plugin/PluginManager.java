package cz.muni.physics.pdr.plugin;

import cz.muni.physics.pdr.entity.Plugin;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * @author Michal Krajčovič
 * @version 1.0
 * @since 21/03/16
 */
public interface PluginManager<T> {
    CompletableFuture<List<T>> run(Plugin plugin, Map<String, String> params);

}
