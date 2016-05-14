package cz.muni.physics.pdr.swasp;

import cz.muni.physics.pdr.java.Plugin;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

/**
 * @author Michal Krajčovič
 * @version 1.0
 * @since 12/04/16
 */
public class Main {
    public static void main(String[] args) {
        URL u = null;
        try {
            new URL(args[0]); // je to url ?
            Document doc;
            try {
                doc = Jsoup.connect(args[0]).get();
            } catch (IOException e) {
                return;
            }
            for (Element a : doc.getElementsByTag("a")) {
                if (a.ownText().equals("CSV")) {
                    try {
                        u = new URL("http://wasp.cerit-sc.cz" + a.attr("href"));
                    } catch (MalformedURLException e) {
                        return;
                    }
                }
            }
        } catch (MalformedURLException e) {
            try {
                u = new URL("http://wasp.cerit-sc.cz/json?type=CSV&object=" + URLEncoder.encode(args[0], "UTF-8"));
            } catch (IOException exc) {
                exc.printStackTrace();
                return;
            }
        }

        if (u == null) return;
        Plugin plugin = new SwaspPlugin();
        plugin.getDataFromUrl(u.toString()).forEach(d -> System.out.println(d.toCsv()));
    }
}
