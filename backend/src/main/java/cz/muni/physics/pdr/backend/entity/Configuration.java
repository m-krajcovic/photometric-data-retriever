package cz.muni.physics.pdr.backend.entity;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * @author Michal Krajčovič
 * @version 1.0
 * @since 23/04/16
 */
@XStreamAlias("configuration")
public class Configuration {
    @XStreamAlias("starSurveys")
    private List<StarSurvey> starSurveys;
    @XStreamAlias("patterns")
    private Map<String, Pattern> patterns;
    @XStreamAlias("valueParameters")
    private Map<String, String> valueParameters;

    public List<StarSurvey> getStarSurveys() {
        return starSurveys;
    }

    public void setStarSurveys(List<StarSurvey> starSurveys) {
        this.starSurveys = starSurveys;
    }

    public Map<String, Pattern> getPatterns() {
        return patterns;
    }

    public void setPatterns(Map<String, Pattern> patterns) {
        this.patterns = patterns;
    }

    public Map<String, String> getValueParameters() {
        return valueParameters;
    }

    public void setValueParameters(Map<String, String> valueParameters) {
        this.valueParameters = valueParameters;
    }
}
