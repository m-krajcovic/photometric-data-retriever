package cz.muni.physics.pdr.plugin;

import cz.muni.physics.pdr.java.PhotometricData;
import cz.muni.physics.pdr.utils.ParameterUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.text.StrSubstitutor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * @author Michal Krajčovič
 * @version 1.0
 * @since 11/04/16
 */
public class PhotometricDataPluginStarter implements PluginStarter<PhotometricData> {

    private final static Logger logger = LogManager.getLogger(PhotometricDataPluginStarter.class);

    private String command;
    private Map<String, String> parameters;
    private boolean readyToRun = false;

    @Override
    public boolean prepare(String command, Map<String, String> parameters) {
        this.command = command;
        this.parameters = parameters;
        return (readyToRun = ParameterUtils.isResolvableWithParameters(command, parameters));
    }

    @Override
    public Process run() throws IOException {
        logger.debug("Started run() for ");
        if (!readyToRun)
            throw new IllegalStateException("Plugin must be prepared first by preparePlugin() method.");
        readyToRun = false;
        String resolvedCommand = StrSubstitutor.replace(command, parameters);
        return Runtime.getRuntime().exec(resolvedCommand);
    }

    @Override
    public List<PhotometricData> runForResult() {
        List<PhotometricData> result = new ArrayList<>();
        Process p;
        try {
            p = run();
        } catch (IOException e) {
            e.printStackTrace(); // todo
            return result;
        }

        try {
            CompletableFuture.supplyAsync(new StreamGobbler(p.getErrorStream()));
            result.addAll(CompletableFuture.supplyAsync(new StreamGobbler<>(p.getInputStream(), line -> {
                String[] split = line.split(",");
                if (split.length >= 3 && NumberUtils.isParsable(split[0])
                        && NumberUtils.isParsable(split[1]) && NumberUtils.isParsable(split[2])) {
                    return new PhotometricData(split[0], split[1], split[2]);
                }
                return null;
            })).get());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return result;
    }


}
