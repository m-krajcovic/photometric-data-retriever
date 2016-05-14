package cz.muni.physics.pdr.nsvs;

import cz.muni.physics.pdr.java.PhotometricData;
import cz.muni.physics.pdr.java.Plugin;
import cz.muni.physics.pdr.java.PluginUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Michal Krajčovič
 * @version 1.0
 * @since 25/03/16
 */
public class NsvsPlugin implements Plugin {

    @Override
    public List<PhotometricData> getDataFromUrl(String url) {
        List<PhotometricData> result = new ArrayList<>();
        Document doc;
        try {
            Jsoup.connect(url).get();
            Connection.Response res = Jsoup.connect(url).execute();
            doc = Jsoup.connect("http://skydot.lanl.gov/nsvs/print_light_curve.php").cookies(res.cookies()).get();
        } catch (IOException e) {
            e.printStackTrace();
            return result;
        }
        Elements trs = doc.getElementsByTag("tr");
        trs.remove(0);
        for (Element tr : trs) {
            Double julianDate = Double.parseDouble(tr.child(0).text()) + 2450000.5;
            PhotometricData data = new PhotometricData(julianDate.toString(), tr.child(1).text(), tr.child(2).text());
            result.add(data);
        }
        return result;
    }
}
