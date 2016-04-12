package cz.muni.physics.pdr.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * @author Michal Krajčovič
 * @version 1.0
 * @since 29/03/16
 */
public class Plugin {
    private StringProperty name = new SimpleStringProperty("");
    private StringProperty mainFile = new SimpleStringProperty("");
    private StringProperty command = new SimpleStringProperty("");

    public Plugin() {
    }

    public Plugin(String name, String mainFile, String command) {
        this.name.setValue(name);
        this.mainFile.setValue(mainFile);
        this.command.setValue(command);
    }

    public Plugin(String pluginName) {
        this.name.setValue(pluginName);
    }

    public String getName() {
        return name.get();
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public StringProperty nameProperty() {
        return name;
    }

    public String getMainFile() {
        return mainFile.get();
    }

    public void setMainFile(String mainFile) {
        this.mainFile.set(mainFile);
    }

    public StringProperty mainFileProperty() {
        return mainFile;
    }

    public String getCommand() {
        return command.get();
    }

    public void setCommand(String command) {
        this.command.set(command);
    }

    public StringProperty commandProperty() {
        return command;
    }

    @Override
    public String toString() {
        return "Plugin{" +
                "name=" + name +
                ", mainFile=" + mainFile +
                ", command=" + command +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Plugin plugin = (Plugin) o;

        if (getName() != null ? !getName().equals(plugin.getName()) : plugin.getName() != null) return false;
        return getMainFile() != null ? getMainFile().equals(plugin.getMainFile()) : plugin.getMainFile() == null && (getCommand() != null ? getCommand().equals(plugin.getCommand()) : plugin.getCommand() == null);
    }

    @Override
    public int hashCode() {
        int result = getName() != null ? getName().hashCode() : 0;
        result = 31 * result + (getMainFile() != null ? getMainFile().hashCode() : 0);
        result = 31 * result + (getCommand() != null ? getCommand().hashCode() : 0);
        return result;
    }
}
