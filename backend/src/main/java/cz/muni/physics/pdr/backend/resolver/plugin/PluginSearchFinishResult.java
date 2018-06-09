package cz.muni.physics.pdr.backend.resolver.plugin;

public class PluginSearchFinishResult {
    private String starSurveyName;
    private int foundCount;

    public PluginSearchFinishResult(String starSurveyName, int foundCount) {
        this.starSurveyName = starSurveyName;
        this.foundCount = foundCount;
    }

    public String getStarSurveyName() {
        return starSurveyName;
    }

    public void setStarSurveyName(String starSurveyName) {
        this.starSurveyName = starSurveyName;
    }

    public int getFoundCount() {
        return foundCount;
    }

    public void setFoundCount(int foundCount) {
        this.foundCount = foundCount;
    }
}
