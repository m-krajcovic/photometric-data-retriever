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
        return Runtime.getRuntime().exec(resolveCommand(plugin, params));
    }

    /**
     * java -jar ${mainFile} ${param1} ${param2} ${param3}
     *
     * @param plugin
     * @param params
     * @return
     */
    private String resolveCommand(Plugin plugin, Map<String, String> params) {
        params.put("mainFile", resolveMainFilePath(plugin));
        return StrSubstitutor.replace(plugin.getCommand(), params);
    }

    private String resolveMainFilePath(Plugin plugin) {
        return pluginsDirPath + File.separator + plugin.getName() + File.separator + plugin.getMainFile();
    }
}
