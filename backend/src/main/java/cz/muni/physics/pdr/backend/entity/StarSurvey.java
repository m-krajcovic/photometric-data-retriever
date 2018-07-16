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
    @XStreamAsAttribute
    private boolean enabled;

    public StarSurvey() {
    }

    public StarSurvey(StarSurvey survey) {
        this.name = survey.name;
        this.plugin = new Plugin(survey.plugin);
        this.enabled = survey.enabled;
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

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
