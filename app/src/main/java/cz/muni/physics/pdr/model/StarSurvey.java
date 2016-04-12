package cz.muni.physics.pdr.model;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;

import java.util.regex.Pattern;

/**
 * @author Michal Krajčovič
 * @version 1.0
 * @since 31/03/16
 */
public class StarSurvey {
    private StringProperty name = new SimpleStringProperty("");
    private ObjectProperty<Plugin> plugin = new SimpleObjectProperty<>();

    private ObservableList<Pattern> regexPatterns = FXCollections.observableArrayList(); // napr NSVS\s(?<id>\d*) ~> match on name/coord resolved results
    private ObservableMap<String, String> valueParameters = FXCollections.observableHashMap(); // napr. <"radec", "${ra};${dec}"> ~> 188.7;+25.3
    private ObservableList<String> urls = FXCollections.observableArrayList(); // napr. <1, "www.google.com?query={radec}">, <2, "www.google.com?id={id}&ra={ra}"> -> get first where all \{.*\} exists in parameter map

    public StarSurvey(String name, Plugin plugin) {
        this.name.setValue(name);
        this.plugin.setValue(plugin);
    }

    public StarSurvey() {
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

    public Plugin getPlugin() {
        return plugin.get();
    }

    public void setPlugin(Plugin plugin) {
        this.plugin.set(plugin);
    }

    public ObjectProperty<Plugin> pluginProperty() {
        return plugin;
    }

    public ObservableList<Pattern> getRegexPatterns() {
        return regexPatterns;
    }

    public void setRegexPatterns(ObservableList<Pattern> regexPatterns) {
        this.regexPatterns = regexPatterns;
    }

    public ObservableMap<String, String> getValueParameters() {
        return valueParameters;
    }

    public void setValueParameters(ObservableMap<String, String> valueParameters) {
        this.valueParameters = valueParameters;
    }

    public ObservableList<String> getUrls() {
        return urls;
    }

    public void setUrls(ObservableList<String> urls) {
        this.urls = urls;
    }

    @Override
    public String toString() {
        return "StarSurvey{" +
                "name=" + name +
                ", plugin=" + plugin +
                '}';
    }
}
