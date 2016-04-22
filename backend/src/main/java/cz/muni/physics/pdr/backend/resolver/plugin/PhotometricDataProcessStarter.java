package cz.muni.physics.pdr.backend.resolver.plugin;

import cz.muni.physics.pdr.backend.entity.PhotometricData;
import cz.muni.physics.pdr.backend.utils.ParameterUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.text.StrSubstitutor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author Michal Krajčovič
 * @version 1.0
 * @since 11/04/16
 */
public class PhotometricDataProcessStarter implements ProcessStarter<PhotometricData> {

    private final static Logger logger = LogManager.getLogger(PhotometricDataProcessStarter.class);

    private String command;
    private Map<String, String> parameters;
    private boolean readyToRun = false;

    @Override
    public boolean prepare(String command, Map<String, String> parameters) {
        if (command == null) {
            throw new IllegalArgumentException("command cannot be null.");
        }
        if (parameters == null) {
            throw new IllegalArgumentException("parameters cannot be null.");
        }
        logger.debug("Preparing command '{}'", command);
        this.command = command;
        this.parameters = parameters;
        return (readyToRun = ParameterUtils.isResolvableWithParameters(command, parameters));
    }

    @Override
    public boolean prepare(List<String> commands, Map<String, String> parameters) {
        if (commands == null) {
            throw new IllegalArgumentException("commands cannot be null.");
        }
        if (commands.isEmpty()) {
            throw new IllegalArgumentException("commands cannot be empty.");
        }
        if (parameters == null) {
            throw new IllegalArgumentException("parameters cannot be null.");
        }
        logger.debug("Searching for resolvable command from {}", commands);
        Optional<String> possibleCommand = commands.stream().filter(c -> ParameterUtils.isResolvableWithParameters(c, parameters)).findFirst();
        return possibleCommand.isPresent() && prepare(possibleCommand.get(), parameters);
    }

    @Override
    public Process run() throws IOException {
        if (!readyToRun)
            throw new IllegalStateException("Plugin must be prepared first by preparePlugin() method.");

        String[] commandSplit = command.split(" ");
        for (int i = 0; i < commandSplit.length; i++) {
            commandSplit[i] = StrSubstitutor.replace(commandSplit[i], parameters);
        }
        logger.debug("Starting process by command '{}'", (Object) commandSplit);
        return Runtime.getRuntime().exec(commandSplit);
    }

    @Override
    public List<PhotometricData> runForResult() {
        List<PhotometricData> result = new ArrayList<>();
        Process p;
        try {
            p = run();
        } catch (IOException e) {
            throw new RuntimeException("Failed to run process by command {}" + command, e);
        }

        StreamGobbler<PhotometricData> stdOutput = new StreamGobbler<>(p.getInputStream(), line -> {
            String[] split = line.split(",");
            if (split.length >= 3 && NumberUtils.isParsable(split[0])
                    && NumberUtils.isParsable(split[1]) && NumberUtils.isParsable(split[2])) {
                return new PhotometricData(split[0], split[1], split[2]);
            }
            return null;
        });
        StreamGobbler errorOutput = new StreamGobbler<>(p.getErrorStream(), line -> {
            logger.error(line);
            return null;
        });

        result.addAll(stdOutput.get());
        return result;
    }

}