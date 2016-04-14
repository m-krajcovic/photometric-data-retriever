package cz.muni.physics.pdr.model;

import cz.muni.physics.pdr.entity.Plugin;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * @author Michal Krajčovič
 * @version 1.0
 * @since 29/03/16
 */
public class PluginModel implements EntityModel<Plugin>{
    private StringProperty name = new SimpleStringProperty("");
    private StringProperty mainFile = new SimpleStringProperty("");
    private ObservableList<String> commands = FXCollections.observableArrayList();

    public PluginModel() {
    }

    public PluginModel(String name, String mainFile, String command) {
        this.name.setValue(name);
        this.mainFile.setValue(mainFile);
        this.commands.add(command);
    }

    public PluginModel(String name, String mainFile, String... commands) {
        this.name.setValue(name);
        this.mainFile.setValue(mainFile);
        this.commands.addAll(commands);
    }

    public PluginModel(String pluginName) {
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

    public ObservableList<String> getCommands() {
        return commands;
    }

    public void setCommands(ObservableList<String> commands) {
        this.commands = commands;
    }

    @Override
    public String toString() {
        return "PluginModel{" +
                "name=" + name +
                ", mainFile=" + mainFile +
                '}';
    }

    @Override
    public Plugin toEntity() {
        return new Plugin(getName(), getMainFile(), getCommands());
    }
}
