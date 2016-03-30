package cz.muni.physics.plugin;

import javafx.beans.property.*;
import javafx.scene.control.Hyperlink;

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
    private BooleanProperty enabled;

    public Plugin(String name, String URL, String mainFile, String command, String path, boolean enabled) {
        this.name = new SimpleStringProperty(name);
        this.URL = new SimpleObjectProperty<Hyperlink>(new Hyperlink(URL));
        this.mainFile = new SimpleStringProperty(mainFile);
        this.command = new SimpleStringProperty(command);
        this.path = new SimpleStringProperty(path);
        this.enabled = new SimpleBooleanProperty(enabled);
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

    public boolean getEnabled() {
        return enabled.get();
    }

    public BooleanProperty enabledProperty() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled.set(enabled);
    }
}
