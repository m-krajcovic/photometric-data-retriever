package cz.muni.physics.pdr.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * @author Michal Krajčovič
 * @version 1.0
 * @since 29/03/16
 */
public class StarSurveyPlugin {
    private StringProperty name;
    private StringProperty mainFile;
    private StringProperty command;

    public StarSurveyPlugin() {
        this.name = new SimpleStringProperty();
        this.mainFile = new SimpleStringProperty();
        this.command = new SimpleStringProperty();
    }

    public StarSurveyPlugin(String name, String mainFile, String command) {
        this.name = new SimpleStringProperty(name);
        this.mainFile = new SimpleStringProperty(mainFile);
        this.command = new SimpleStringProperty(command);
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
        return "StarSurveyPlugin{" +
                "name=" + name +
                ", mainFile=" + mainFile +
                ", command=" + command +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StarSurveyPlugin starSurveyPlugin = (StarSurveyPlugin) o;

        if (getName() != null ? !getName().equals(starSurveyPlugin.getName()) : starSurveyPlugin.getName() != null) return false;
        return getMainFile() != null ? getMainFile().equals(starSurveyPlugin.getMainFile()) : starSurveyPlugin.getMainFile() == null && (getCommand() != null ? getCommand().equals(starSurveyPlugin.getCommand()) : starSurveyPlugin.getCommand() == null);
    }

    @Override
    public int hashCode() {
        int result = getName() != null ? getName().hashCode() : 0;
        result = 31 * result + (getMainFile() != null ? getMainFile().hashCode() : 0);
        result = 31 * result + (getCommand() != null ? getCommand().hashCode() : 0);
        return result;
    }
}
