package cz.muni.physics.pdr.ztf;

import cz.muni.physics.pdr.java.PluginUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URL;
import java.text.MessageFormat;

/**
 * @author Michal Krajčovič
 * @version 1.0
 * @since 21/04/16
 */
public class Main {

    public static void main(String[] args) throws IOException {
        // https://irsa.ipac.caltech.edu/cgi-bin/ZTF/nph_light_curves?POS=CIRCLE%20298.0025%2029.87147%200.0014
//        args = new String[]{"298.0025", "29.87147"};
        URL url;
        if (args.length == 2) { // su to suradnice
            url = new URL(
                    MessageFormat.format("https://irsa.ipac.caltech.edu/cgi-bin/ZTF/nph_light_curves?POS=CIRCLE%20{0}%20{1}%200.0014", args[0], args[1]));
        } else {
            return;
        }

        Document doc = Jsoup.parse(PluginUtils.copyUrlToFile(url, "ZTF-" + System.currentTimeMillis() + ".tbl", 3), null);
        Element tableData = doc.getElementsByTag("tbody").first();

        Elements rows = tableData.getElementsByTag("TR");
        for (Element row : rows) {
            Elements cells = row.getElementsByTag("TD");
            // 2, 4, 5
            if (cells.size() > 5) {
                String jd = cells.get(2).text();
                String mag = cells.get(4).text();
                String err = cells.get(5).text();
                System.out.println(MessageFormat.format("{0},{1},{2}", jd, mag, err));
            }
        }
    }
}

