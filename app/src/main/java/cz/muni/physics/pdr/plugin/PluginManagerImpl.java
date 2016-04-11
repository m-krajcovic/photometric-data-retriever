package cz.muni.physics.pdr.plugin;

import cz.muni.physics.pdr.model.Plugin;
import cz.muni.physics.pdr.utils.ParameterUtils;
import org.apache.commons.lang3.text.StrSubstitutor;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * @author Michal Krajčovič
 * @version 1.0
 * @since 08/04/16
 */
public class PluginManagerImpl implements PluginManager { // TODO make this into prototype, PluginStarter or whatever

    private String pluginsDirPath;
    private boolean readyToRun = false;

    public PluginManagerImpl(String pluginsDirPath) {
        this.pluginsDirPath = pluginsDirPath;
    }

    @Override
    public Process run(Plugin plugin, Map<String, String> params) throws IOException {
        if(!readyToRun)
            throw new IllegalStateException("Plugin must be prepared first by preparePlugin() method.");
        readyToRun = false;
        String command = StrSubstitutor.replace(plugin.getCommand(), params);
        return Runtime.getRuntime().exec(command);
    }

    @Override
    public boolean preparePlugin(Plugin plugin, Map<String, String> params) {
        params.put("mainFile", resolveMainFilePath(plugin));
        if (ParameterUtils.isResolvableWithParameters(plugin.getCommand(), params)){
            readyToRun = true;
            return true;
        }
        return false;
    }

    private String resolveMainFilePath(Plugin plugin) {
        return pluginsDirPath + File.separator + plugin.getName() + File.separator + plugin.getMainFile();
    }
}
