package cz.muni.physics.model;

import cz.muni.physics.storage.DataStorage;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.io.File;
import java.text.MessageFormat;

/**
 * @author Michal Krajčovič
 * @version 1.0
 * @since 29/03/16
 */
public class Plugin {
    private StringProperty name;
    private StringProperty mainFile;
    private StringProperty command;

    public Plugin() {
    }

    public Plugin(String name, String mainFile, String command) {
        this.name = new SimpleStringProperty(name);
        this.mainFile = new SimpleStringProperty(mainFile);
        this.command = new SimpleStringProperty(command);
    }

    public String getFullCommand(String url){
        return MessageFormat.format(getCommand(), DataStorage.getPluginsDir().getAbsolutePath() + File.separator + getName() + File.separator + getMainFile(), url);
    }

    public String getName() {
        return name.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public String getMainFile() {
        return mainFile.get();
    }

    public StringProperty mainFileProperty() {
        return mainFile;
    }

    public void setMainFile(String mainFile) {
        this.mainFile.set(mainFile);
    }

    public String getCommand() {
        return command.get();
    }

    public StringProperty commandProperty() {
        return command;
    }

    public void setCommand(String command) {
        this.command.set(command);
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
        if (getMainFile() != null ? !getMainFile().equals(plugin.getMainFile()) : plugin.getMainFile() != null)
            return false;
        return getCommand() != null ? getCommand().equals(plugin.getCommand()) : plugin.getCommand() == null;
    }

    @Override
    public int hashCode() {
        int result = getName() != null ? getName().hashCode() : 0;
        result = 31 * result + (getMainFile() != null ? getMainFile().hashCode() : 0);
        result = 31 * result + (getCommand() != null ? getCommand().hashCode() : 0);
        return result;
    }
}
