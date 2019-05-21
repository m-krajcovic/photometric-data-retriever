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
//        URL url;
        String urlString;
        if (args.length == 2) { // su to suradnice
            urlString =
                    MessageFormat.format("https://irsa.ipac.caltech.edu/cgi-bin/ZTF/nph_light_curves?POS=CIRCLE%20{0}%20{1}%200.0003", args[0], args[1]);
        } else {
            return;
        }

        for (String bandname: new String[] {"r", "g", "i"}) {
            URL url = new URL(MessageFormat.format(urlString + "&BANDNAME={0}", bandname));
            Document doc = Jsoup.parse(PluginUtils.copyUrlToFile(url, "ZTF-" + bandname + "-" + System.currentTimeMillis() + ".tbl", 3), null);
            Element tableData = doc.getElementsByTag("tbody").first();

            Elements rows = tableData.getElementsByTag("TR");
            String oid = "";
            for (Element row : rows) {
                Elements cells = row.getElementsByTag("TD");
                // 2, 4, 5
                if (cells.size() > 5) {
                    if (oid.isEmpty()) {
                        oid = cells.get(0).text();
                    } else if (!cells.get(0).text().equals(oid)) {
                        break;
                    }
                    String jd = cells.get(2).text();
                    String mag = cells.get(4).text();
                    String err = cells.get(5).text();
                    System.out.println(MessageFormat.format("{0},{1},{2},{3}", jd, mag, err, bandname));
                }
            }
        }
    }
}

