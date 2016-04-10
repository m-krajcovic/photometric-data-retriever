package cz.muni.physics.plugin;

import cz.muni.physics.model.Plugin;
import org.apache.commons.lang3.text.StrSubstitutor;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * @author Michal Krajčovič
 * @version 1.0
 * @since 08/04/16
 */
public class PluginManagerImpl implements PluginManager {

    private String pluginsDirPath;

    public PluginManagerImpl(String pluginsDirPath) {
        this.pluginsDirPath = pluginsDirPath;
    }

    @Override
    public Process run(Plugin plugin, Map<String, String> params) throws IOException {
        params.put("mainFile", resolveMainFilePath(plugin));
        String command = StrSubstitutor.replace(plugin.getCommand(), params);
        return Runtime.getRuntime().exec(command);
    }

    private String resolveMainFilePath(Plugin plugin) {
        return pluginsDirPath + File.separator + plugin.getName() + File.separator + plugin.getMainFile();
    }
}
