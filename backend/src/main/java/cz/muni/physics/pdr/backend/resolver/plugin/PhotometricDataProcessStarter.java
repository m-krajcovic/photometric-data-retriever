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
import java.util.stream.Collectors;

/**
 * @author Michal Krajčovič
 * @version 1.0
 * @since 11/04/16
 */
public class PhotometricDataProcessStarter implements ProcessStarter<List<PhotometricData>> {

    private static final Logger logger = LogManager.getLogger(PhotometricDataProcessStarter.class);

    private String command;
    private Map<String, String> parameters;
    private boolean readyToRun = false;
    private double minThreshold;
    private double maxThreshold;

    public PhotometricDataProcessStarter() {
        minThreshold = Double.NEGATIVE_INFINITY;
        maxThreshold = Double.POSITIVE_INFINITY;
    }

    public PhotometricDataProcessStarter(double minThreshold, double maxThreshold) {
        this.minThreshold = minThreshold;
        this.maxThreshold = maxThreshold;
    }

    @Override
    public boolean prepare(String command, Map<String, String> parameters) {
        if (command == null) {
            throw new IllegalArgumentException("command cannot be null.");
        }
        if (parameters == null) {
            throw new IllegalArgumentException("parameters cannot be null.");
        }
        logger.debug("Preparing command '{}' with parameters '{}'", command, String.join(", ", parameters.entrySet().stream().map(n -> n.getKey() + ": " + n.getValue()).collect(Collectors.joining(", "))));
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

        String[] commandSplit = command.trim().split(" ");
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
            throw new RuntimeException("Failed to run process by command " + command, e);
        }

        StreamGobbler<PhotometricData> stdOutput = new StreamGobbler<>(p.getInputStream(), line -> {
            String[] split = line.split(",");
            if (split.length >= 3 && NumberUtils.isParsable(split[0])
                    && NumberUtils.isParsable(split[1]) && NumberUtils.isParsable(split[2])) {
                PhotometricData data = new PhotometricData(split[0], split[1], split[2]);
                if (data.getMagnitude() >= minThreshold && data.getMagnitude() <= maxThreshold) {
                    if (split.length >= 4) {
                        data.setId(split[3]);
                    }
                    return data;
                }
            }
            return null;
        });

        result.addAll(stdOutput.get());
        return result;
    }

}
