package cz.muni.physics.pdr.kws;

import cz.muni.physics.pdr.java.AstroUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.text.ParseException;
import java.util.Iterator;

/**
 * @author Michal Krajcovic <mkrajcovic@mail.muni.cz>
 */
public class Main {
    public static void main(String[] args) throws IOException {
        if (args.length == 0) System.exit(0);

        String id = args[0];

        Document document = Jsoup.connect("http://kws.cetus-net.org/~maehara/VSdata.py").data(
                "object", id,
                "resolver", "simbad",
                "p_band", "All",
                "plot", "0",
                "obs_ys", "",
                "obs_ms", "",
                "obs_ds", "",
                "obs_ye", "",
                "obs_me", "",
                "obs_de", "",
                "submit", "Send query"
        ).timeout(15000).get();
        Element table = document.getElementsByTag("table").first().children().first();
        if (table != null) {
            Elements rows = table.getElementsByTag("tr");
            Iterator<Element> iterator = rows.iterator();
            if (iterator.hasNext()) iterator.next();
            while (iterator.hasNext()) {
                Element tr = iterator.next();
                Elements cols = tr.getElementsByTag("td");
                if (cols.size() >= 5) {
                    String date = cols.get(1).text();
                    double jd;
                    try {
                        jd = AstroUtils.dateToJulian(date);
                    } catch (ParseException e) {
                        continue;
                    }
                    String mag = cols.get(2).text();
                    String err = cols.get(3).text();
                    String band = cols.get(4).text();
                    System.out.println(jd + "," + mag + "," + err + "," + band);
                }
            }
        }

    }
}
