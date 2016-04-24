package cz.muni.physics.pdr.backend.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Michal Krajčovič
 * @version 1.0
 * @since 13/04/16
 */
public class StellarObject {
    private List<String> names = new ArrayList<>();
    private Map<String, String> ids = new HashMap<>();
    private Double rightAscension;
    private Double declination;
    private Double epoch;
    private Double period;
    private Double distance;

    public void merge(StellarObject other) {
        if (other != null) {
            this.rightAscension = this.rightAscension == null ? other.rightAscension : this.rightAscension;
            this.declination = this.declination == null ? other.declination : this.declination;
            this.names.addAll(other.names);
            this.epoch = this.epoch == null ? other.epoch : this.epoch;
            this.period = this.period == null ? other.period : this.period;
            this.ids.putAll(other.ids);
            this.distance = this.distance == null ? other.distance : this.distance;
        }
    }

    public Double getDeclinationInHours(){
        return declination / 15;
    }

    public Double getRightAscensionInHours(){
        return rightAscension / 15;
    }

    public Double getDeclination() {
        return declination;
    }

    public void setDeclination(Double declination) {
        this.declination = declination;
    }

    public Double getRightAscension() {
        return rightAscension;
    }

    public void setRightAscension(Double rightAscension) {
        this.rightAscension = rightAscension;
    }

    public List<String> getNames() {
        return names;
    }

    public void setNames(List<String> names) {
        this.names = names;
    }

    public Double getEpoch() {
        return epoch;
    }

    public void setEpoch(Double epoch) {
        this.epoch = epoch;
    }

    public Double getPeriod() {
        return period;
    }

    public void setPeriod(Double period) {
        this.period = period;
    }

    public Map<String, String> getIds() {
        return ids;
    }

    public void setIds(Map<String, String> ids) {
        this.ids = ids;
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    @Override
    public String toString() {
        return "StellarObject{" +
                "names=[ " + String.join(", ", names) +
                " ], rightAscension='" + rightAscension + '\'' +
                ", declination='" + declination + '\'' +
                ", epoch='" + epoch + '\'' +
                ", period='" + period + '\'' +
                ", distance= '" + distance + '\'' +
                '}';
    }

    public String toLines() {
        String output = "";
        output += String.join("\n", names);
        for (Map.Entry<String, String> id : ids.entrySet()){
            output += id.getKey() + ":" + id.getValue() + "\n";
        }
        output += rightAscension + "\n"
                + declination + "\n"
                + period + "\n"
                + epoch + "\n";
        return output;
    }
}
