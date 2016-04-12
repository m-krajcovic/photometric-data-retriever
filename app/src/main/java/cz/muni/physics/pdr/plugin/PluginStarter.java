package cz.muni.physics.pdr.plugin;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author Michal Krajčovič
 * @version 1.0
 * @since 11/04/16
 */
public interface PluginStarter<T> {
    boolean prepare(String command, Map<String, String> parameters);

    Process run() throws IOException;

    List<T> runForResult();
}
