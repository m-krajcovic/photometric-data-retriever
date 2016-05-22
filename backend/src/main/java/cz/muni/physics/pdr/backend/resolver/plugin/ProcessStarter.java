package cz.muni.physics.pdr.backend.resolver.plugin;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Class to prepare and run processes
 * @author Michal Krajčovič
 * @version 1.0
 * @since 11/04/16
 */
public interface ProcessStarter<T> {
    /**
     * This method prepares given command with parameters
     * @param command string command with parameters, e.g. 'java -jar ${mainFile}
     * @param parameters map with parameters
     * @return Returns true only if every parameter in command can be replaced with parameter from map
     */
    boolean prepare(String command, Map<String, String> parameters);

    /**
     * This method is for checking if any of commands from given list is resolvable with parameters from given map.
     * Uses first available command
     * @param commands command list
     * @param parameters parameters map
     * @return Returns true if at least one of commands is resolvable with parameters
     */
    boolean prepare(List<String> commands, Map<String, String> parameters);

    /**
     * This method can be called only after running one of prepare methods which resulted with TRUE
     * @return Process object that was run by given command from prepare method
     * @throws IOException
     */
    Process run() throws IOException;

    /**
     * Can be called only after one of prepare method returned true
     * This method runs command in new process and returns data from it's output
     * @return Output data from process
     */
    T runForResult();
}
