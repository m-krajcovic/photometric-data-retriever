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
        Document doc;
        if (args.length == 1) {
            String objName = args[0];
            u = new URL("http://wasp.cerit-sc.cz/json?type=CSV&object=" + URLEncoder.encode(objName, "UTF-8"));
            doc = Jsoup.connect("https://wasp.cerit-sc.cz/search")
                    .data("objid", objName)
                    .data("limit", "1")
                    .data("radius", "1")
                    .data("radiusUnit", "min")
                    .post();
        } else if (args.length == 2) {
            String ra = args[0];
            String dec = args[1];
            String resultURL = "https://wasp.cerit-sc.cz/search?ra=" + ra + "&dec=" + dec + "&radius=1&radiusUnit=min&limit=1";
            doc = Jsoup.connect(resultURL).get();
        } else {
            return;
        }

        for (Element a : doc.getElementsByTag("a")) {
            if (a.ownText().equals("CSV")) {
                u = new URL(a.attr("abs:href"));
                break;
            }
        }

        if (u == null) return;
        Plugin plugin = new SwaspPlugin();
        plugin.getDataFromUrl(u.toString()).forEach(d -> System.out.println(d.toCsv()));
    }
}
