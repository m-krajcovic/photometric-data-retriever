package cz.muni.physics.pdr.crts;

import cz.muni.physics.pdr.java.Plugin;

import java.text.MessageFormat;

/**
 * @author Michal Krajčovič
 * @version 1.0
 * @since 31/03/16
 */
public class Main {
    public static void main(String[] args) {
        String url;
        if (args.length == 2) { // je to ra dec
            url = MessageFormat.format("http://nunuku.caltech.edu/cgi-bin/getcssconedb_release_img.cgi?RA={0}&amp;Dec={1}&amp;Rad=0.2&amp;DB=photcat&amp;OUT=csv&amp;SHORT=short&amp;PLOT=plot", args[0], args[1]);
        } else {
            return;
        }
        Plugin plugin = new CrtsPlugin();
        plugin.getDataFromUrl(url).forEach(d -> System.out.println(d.getJulianDate() + "," + d.getMagnitude() + "," + d.getError()));
    }
}
