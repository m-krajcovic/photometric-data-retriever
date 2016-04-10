package cz.muni.physics.pdr.nameresolver;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Michal Krajčovič
 * @version 1.0
 * @since 27/03/16
 */
public class NameResolverResult {
    private List<String> names = new ArrayList<>();
    private String jpos;
    private String jraddeg;
    private String jdedeg;

    public void merge(NameResolverResult other) {
        this.jpos = this.jpos == null ? other.jpos : this.jpos;
        this.jraddeg = this.jraddeg == null ? other.jraddeg : this.jraddeg;
        this.jdedeg = this.jdedeg == null ? other.jdedeg : this.jdedeg;
        this.names.addAll(other.names);
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

    @Override
    public String toString() {
        return "NameResolverResult{" +
                "names=" + StringUtils.join(", ", names) +
                ", jpos='" + jpos + '\'' +
                ", jraddeg='" + jraddeg + '\'' +
                ", jdedeg='" + jdedeg + '\'' +
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
