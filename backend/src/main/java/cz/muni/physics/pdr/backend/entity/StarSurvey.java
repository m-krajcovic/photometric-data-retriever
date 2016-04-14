package cz.muni.physics.pdr.backend.entity;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
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
    @XStreamAlias("patterns")
    private List<Pattern> regexPatterns;
    @XStreamAlias("valueParams")
    private Map<String, String> valueParameters;

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
        if (survey.valueParameters != null)
            this.valueParameters = new HashMap<>(survey.valueParameters);
        if (survey.regexPatterns != null){
            this.regexPatterns = new ArrayList<>();
            survey.regexPatterns.forEach(p -> this.regexPatterns.add(Pattern.compile(p.pattern())));
        }
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

    public List<Pattern> getRegexPatterns() {
        return regexPatterns;
    }

    public void setRegexPatterns(List<Pattern> regexPatterns) {
        this.regexPatterns = regexPatterns;
    }

    public Map<String, String> getValueParameters() {
        return valueParameters;
    }

    public void setValueParameters(Map<String, String> valueParameters) {
        this.valueParameters = valueParameters;
    }
}
