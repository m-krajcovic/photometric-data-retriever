package cz.muni.physics.pdr.nsvs;

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
        if (args.length == 1) {
            if (args[0].startsWith("http")) { // je to url
                url = args[0];
            } else { // je to id
                url = MessageFormat.format("http://skydot.lanl.gov/nsvs/star.php?num={0}&amp;mask=32004", args[0]);
            }
        } else {
            return;
        }

        Plugin plugin = new NsvsPlugin();
        plugin.getDataFromUrl(url).forEach(d -> System.out.println(d.getJulianDate() + "," + d.getMagnitude() + "," + d.getError()));
    }
}
