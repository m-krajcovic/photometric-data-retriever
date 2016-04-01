package cz.muni.physics.model;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Hyperlink;

import java.text.MessageFormat;

/**
 * @author Michal Krajčovič
 * @version 1.0
 * @since 29/03/16
 */
public class Plugin {
    private StringProperty name;
    private ObjectProperty<Hyperlink> URL;
    private StringProperty mainFile;
    private StringProperty command;
    private StringProperty path;

    public Plugin(String name, String URL, String mainFile, String command, String path) {
        this.name = new SimpleStringProperty(name);
        this.URL = new SimpleObjectProperty<Hyperlink>(new Hyperlink(URL));
        this.mainFile = new SimpleStringProperty(mainFile);
        this.command = new SimpleStringProperty(command);
        this.path = new SimpleStringProperty(path);
    }

    public String getFullCommand(String url){
        return MessageFormat.format(getCommand(), getPath() + getMainFile(), url);
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

    public Hyperlink getURL() {
        return URL.get();
    }

    public ObjectProperty<Hyperlink> URLProperty() {
        return URL;
    }

    public void setURL(Hyperlink URL) {
        this.URL.set(URL);
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

    public String getPath() {
        return path.get();
    }

    public StringProperty pathProperty() {
        return path;
    }

    public void setPath(String path) {
        this.path.set(path);
    }

    @Override
    public String toString() {
        return name.get();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Plugin plugin = (Plugin) o;

        if (name != null ? !name.equals(plugin.name) : plugin.name != null) return false;
        if (URL != null ? !URL.equals(plugin.URL) : plugin.URL != null) return false;
        if (mainFile != null ? !mainFile.equals(plugin.mainFile) : plugin.mainFile != null) return false;
        if (command != null ? !command.equals(plugin.command) : plugin.command != null) return false;
        return path != null ? path.equals(plugin.path) : plugin.path == null;

    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (URL != null ? URL.hashCode() : 0);
        result = 31 * result + (mainFile != null ? mainFile.hashCode() : 0);
        result = 31 * result + (command != null ? command.hashCode() : 0);
        result = 31 * result + (path != null ? path.hashCode() : 0);
        return result;
    }
}
