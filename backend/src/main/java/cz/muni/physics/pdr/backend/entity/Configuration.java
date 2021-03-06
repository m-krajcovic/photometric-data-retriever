package cz.muni.physics.pdr.backend.entity;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Data class to store information from configuration file
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
}
