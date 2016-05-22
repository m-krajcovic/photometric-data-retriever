package cz.muni.physics.pdr.app.model;

import cz.muni.physics.pdr.backend.entity.StarSurvey;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;

/**
 * @author Michal Krajčovič
 * @version 1.0
 * @since 31/03/16
 */
public class StarSurveyModel {
    private StringProperty name = new SimpleStringProperty("");
    private ObjectProperty<PluginModel> plugin = new SimpleObjectProperty<>();
    private ObservableList<String> urls = FXCollections.observableArrayList();

    public StarSurveyModel(StarSurvey survey) {
        this(survey.getName(), survey.getPlugin() != null ? new PluginModel(survey.getPlugin()) : null);
        urls.addAll(survey.getUrls());
    }

    public StarSurveyModel(String name, PluginModel plugin) {
        this.name.setValue(name);
        this.plugin.setValue(plugin);
    }

    public StarSurveyModel() {
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

    public PluginModel getPlugin() {
        return plugin.get();
    }

    public void setPlugin(PluginModel plugin) {
        this.plugin.set(plugin);
    }

    public ObjectProperty<PluginModel> pluginProperty() {
        return plugin;
    }


    public ObservableList<String> getUrls() {
        return urls;
    }

    public void setUrls(ObservableList<String> urls) {
        this.urls = urls;
    }

    public StarSurvey toEntity() {
        StarSurvey survey = new StarSurvey();
        survey.setName(getName());
        if (getPlugin() != null) {
            survey.setPlugin(getPlugin().toEntity());
        }
        survey.setUrls(new ArrayList<>(urls));
        return survey;
    }

    @Override
    public String toString() {
        return "StarSurveyModel{" +
                "name=" + name +
                ", plugin=" + plugin +
                '}';
    }
}
