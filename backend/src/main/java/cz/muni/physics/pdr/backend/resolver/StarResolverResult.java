package cz.muni.physics.pdr.backend.resolver;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Michal Krajčovič
 * @version 1.0
 * @since 13/04/16
 */
public class StarResolverResult {
    private List<String> names = new ArrayList<>();
    private String jpos;
    private String jraddeg;
    private String jdedeg;
    private String epoch;
    private String period;

    public void merge(StarResolverResult other) {
        if (other != null) {
            this.jpos = this.jpos == null ? other.jpos : this.jpos;
            this.jraddeg = this.jraddeg == null ? other.jraddeg : this.jraddeg;
            this.jdedeg = this.jdedeg == null ? other.jdedeg : this.jdedeg;
            this.names.addAll(other.names);
            this.epoch = this.epoch == null ? other.epoch : this.epoch;
            this.period = this.period == null ? other.period : this.period;
        }
    }

    public String getJdedeg() {
        return jdedeg;
    }

    public void setJdedeg(String jdedeg) {
        this.jdedeg = jdedeg;
    }

    public String getJraddeg() {
        return jraddeg;
    }

    public void setJraddeg(String jraddeg) {
        this.jraddeg = jraddeg;
    }

    public List<String> getNames() {
        return names;
    }

    public void setNames(List<String> names) {
        this.names = names;
    }

    public String getJpos() {
        return jpos;
    }

    public void setJpos(String jpos) {
        this.jpos = jpos;
    }

    public String getEpoch() {
        return epoch;
    }

    public void setEpoch(String epoch) {
        this.epoch = epoch;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    @Override
    public String toString() {
        return "StarResolverResult{" +
                "names=[ " + String.join(", ", names) +
                " ], jpos='" + jpos + '\'' +
                ", jraddeg='" + jraddeg + '\'' +
                ", jdedeg='" + jdedeg + '\'' +
                ", epoch='" + epoch + '\'' +
                ", period='" + period + '\'' +
                '}';
    }

    public String toLines() {
        String output = "";
        for (String name : names) {
            output += name + "\n";
        }
        output += jpos + "\n"
                + jraddeg + "\n"
                + jdedeg + "\n";
        return output;
    }
}
