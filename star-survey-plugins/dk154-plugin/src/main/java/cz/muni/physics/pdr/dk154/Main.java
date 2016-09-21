package cz.muni.physics.pdr.dk154;

import cz.muni.physics.pdr.java.PluginUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URL;

/**
 * @author Michal
 * @version 1.0
 * @since 9/21/2016
 */
public class Main {
    public static void main(String[] args) throws IOException {

        String input = args.length == 1 ? args[0] : args[0] + " " + args[1];

        Document post = Jsoup.connect("http://vos2.asu.cas.cz/extract_jan16/q/web/form")
                .data("_charset_", "UTF-8",
                        "__nevow_form__", "genForm",
                        "hscs_pos", input,
                        "hscs_sr", "0.05",
//                        "ssa_dateObs", "",
                        "BAND", "ALL",
                        "_DBOPTIONS_ORDER", "",
                        "_DBOPTIONS_DIR", "ASC",
                        "MAXREC", "100",
                        "_FORMAT", "HTML",
//                        "_VERB", "H",
                        "submit", "Go")
                .timeout(10000).post();

        Elements results = post.getElementsByClass("results");
        if (results.size() >= 1) {
            Element table = results.first();
            Elements as = table.getElementsByTag("a");
            for (Element a : as) {
                printData(a.attr("abs:href"));
            }
        }
    }

    private static void printData(String url) {
        String[] split = url.split("/");
        try {
            PluginUtils.copyUrlToFile(new URL(url), split[split.length-1] + ".vot", 20);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
