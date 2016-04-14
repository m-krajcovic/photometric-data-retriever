package cz.muni.physics.pdr.backend.plugin;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;

/**
 * @author Michal Krajčovič
 * @version 1.0
 * @since 11/04/16
 */
public interface ProcessStarter<T> {
    boolean prepare(String command, Map<String, String> parameters);

    boolean prepare(List<String> commands, Map<String, String> parameters);

    Process run() throws IOException;

    List<T> runForResult(Executor executor);
}
