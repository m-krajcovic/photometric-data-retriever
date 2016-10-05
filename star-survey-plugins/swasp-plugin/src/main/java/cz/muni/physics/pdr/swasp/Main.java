package cz.muni.physics.pdr.swasp;

import cz.muni.physics.pdr.java.Plugin;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;

/**
 * @author Michal Krajčovič
 * @version 1.0
 * @since 12/04/16
 */
public class Main {
    public static void main(String[] args) throws IOException {
        URL u = null;

        if (args.length == 1) {
            u = new URL("http://wasp.cerit-sc.cz/json?type=CSV&object=" + URLEncoder.encode(args[0], "UTF-8"));
        } else if (args.length == 2) {
            String ra = args[0];
            String dec = args[1];
            String resultURL = "http://wasp.cerit-sc.cz/search?ra=" + ra + "&dec=" + dec + "&radius=1&radiusUnit=min&limit=1";
            Document doc;
            doc = Jsoup.connect(resultURL).get();
            for (Element a : doc.getElementsByTag("a")) {
                if (a.ownText().equals("CSV")) {
                    u = new URL(a.attr("abs:href"));
                }
            }
        } else {
            return;
        }

        if (u == null) return;
        Plugin plugin = new SwaspPlugin();
        plugin.getDataFromUrl(u.toString()).forEach(d -> System.out.println(d.toCsv()));
    }
}
