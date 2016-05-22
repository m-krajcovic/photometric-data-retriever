package cz.muni.physics.pdr.backend.entity;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Data class for storing information about stellar object
 * @author Michal Krajčovič
 * @version 1.0
 * @since 13/04/16
 */
public class StellarObject {
    private String oName;
    private Set<String> names = new HashSet<>();
    private Map<String, String> ids = new HashMap<>();
    private Double rightAscension;
    private Double declination;
    private Double epoch;
    private Double period;
    private Double distance;

    public void merge(StellarObject other) {
        if (other != null) {
            this.oName = this.oName == null ? other.oName : this.oName;
            this.rightAscension = this.rightAscension == null ? other.rightAscension : this.rightAscension;
            this.declination = this.declination == null ? other.declination : this.declination;
            this.names.addAll(other.names);
            this.epoch = this.epoch == null ? other.epoch : this.epoch;
            this.period = this.period == null ? other.period : this.period;
            this.ids.putAll(other.ids);
            this.distance = this.distance == null ? other.distance : this.distance;
        }
    }

    public double getDeclinationInHours() {
        return declination / 15;
    }

    public double getRightAscensionInHours() {
        return rightAscension / 15;
    }

    public Set<String> getNames() {
        return names;
    }

    public void setNames(Set<String> names) {
        this.names = names;
    }

    public Map<String, String> getIds() {
        return ids;
    }

    public void setIds(Map<String, String> ids) {
        this.ids = ids;
    }

    public Double getRightAscension() {
        return rightAscension;
    }

    public void setRightAscension(Double rightAscension) {
        this.rightAscension = rightAscension;
    }

    public Double getDeclination() {
        return declination;
    }

    public void setDeclination(Double declination) {
        this.declination = declination;
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

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public String getoName() {
        return oName;
    }

    public void setoName(String oName) {
        this.oName = oName;
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
        String output = oName;
        output += String.join("\n", names);
        for (Map.Entry<String, String> id : ids.entrySet()) {
            output += id.getKey() + ":" + id.getValue() + "\n";
        }
        output += rightAscension + "\n"
                + declination + "\n"
                + period + "\n"
                + epoch + "\n";
        return output;
    }
}
