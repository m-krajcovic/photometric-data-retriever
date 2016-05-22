package cz.muni.physics.pdr.backend.entity;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

import java.util.ArrayList;
import java.util.List;

/**
 * Data class for storing information about star survey
 * @author Michal Krajčovič
 * @version 1.0
 * @since 14/04/16
 */
@XStreamAlias("starSurvey")
public class StarSurvey {
    @XStreamAsAttribute
    private String name;
    private Plugin plugin;
    @XStreamAlias("urls")
    private List<String> urls;

    public StarSurvey(String name, Plugin plugin) {
        this.name = name;
        this.plugin = plugin;
    }

    public StarSurvey() {
    }

    public StarSurvey(StarSurvey survey) {
        this.name = survey.name;
        this.plugin = new Plugin(survey.plugin);
        if (survey.urls != null)
            this.urls = new ArrayList<>(survey.urls);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Plugin getPlugin() {
        return plugin;
    }

    public void setPlugin(Plugin plugin) {
        this.plugin = plugin;
    }

    public List<String> getUrls() {
        return urls;
    }

    public void setUrls(List<String> urls) {
        this.urls = urls;
    }
}
