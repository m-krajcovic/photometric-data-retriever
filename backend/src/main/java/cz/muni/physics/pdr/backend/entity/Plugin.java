package cz.muni.physics.pdr.backend.entity;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamOmitField;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Michal Krajčovič
 * @version 1.0
 * @since 14/04/16
 */
@XStreamAlias("plugin")
public class Plugin {
    @XStreamAsAttribute
    private String name;
    @XStreamOmitField
    private String mainFile;
    @XStreamOmitField
    private List<String> commands = new ArrayList<>();

    public Plugin(String name) {
        this.name = name;
    }

    public Plugin(String name, String mainFile, String... commands) {
        this.name = name;
        this.mainFile = mainFile;
        this.commands.addAll(Arrays.asList(commands));
    }

    public Plugin(String name, String mainFile, List<String> commands) {
        this.name = name;
        this.mainFile = mainFile;
        this.commands.addAll(commands);
    }

    public Plugin(Plugin plugin) {
        this.name = plugin.name;
        this.mainFile = plugin.mainFile;
        if (plugin.commands != null)
            this.commands = new ArrayList<>(plugin.commands);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMainFile() {
        return mainFile;
    }

    public void setMainFile(String mainFile) {
        this.mainFile = mainFile;
    }

    public List<String> getCommands() {
        return commands;
    }

    public void setCommands(List<String> commands) {
        this.commands = commands;
    }
}
