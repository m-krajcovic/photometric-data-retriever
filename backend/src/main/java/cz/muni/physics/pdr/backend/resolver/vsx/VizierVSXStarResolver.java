package cz.muni.physics.pdr.backend.resolver.vsx;

import cz.muni.physics.pdr.backend.entity.CelestialCoordinates;
import cz.muni.physics.pdr.backend.entity.StellarObject;
import org.apache.commons.lang3.math.NumberUtils;
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
 * @since 26/04/16
 */
public class VizierVSXStarResolver implements VSXStarResolver {

    private String url;

    public VizierVSXStarResolver(String url) {
        this.url = url;
    }

    @Override
    public List<StellarObject> findByName(String name) {
        List<StellarObject> result = new ArrayList<>();
        try {
            Document doc = getTemplate()
                    .data("-c", name)
                    .post();
            result.addAll(parseDoc(doc));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public List<StellarObject> findByCoordinates(CelestialCoordinates coordinates) {
        List<StellarObject> result = new ArrayList<>();
        try {
            Document doc = getTemplate()
                    .data("-c", String.format("%s %s", coordinates.getRightAscension(), coordinates.getDeclination()))
                    .data("-c.r", Double.toString(coordinates.getRadius()))
                    .post();
            result.addAll(parseDoc(doc));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public boolean isAvailable() {
        return true;
    }

    private List<StellarObject> parseDoc(Document doc) {
        List<StellarObject> result = new ArrayList<>();
        Elements objects = doc.getElementsByClass("tuple-2");
        for (Element object : objects) {
            StellarObject obj = new StellarObject();
            Elements cols = object.getElementsByTag("td");
            obj.setDistance(Double.parseDouble(cols.get(1).text()));
            if (NumberUtils.isParsable(cols.get(4).text()))
                obj.setEpoch(Double.parseDouble(cols.get(4).text()));
            if (NumberUtils.isParsable(cols.get(5).text()))
                obj.setPeriod(Double.parseDouble(cols.get(5).text()));
            obj.setRightAscension(Double.parseDouble(cols.get(6).text()));
            obj.setDeclination(Double.parseDouble(cols.get(7).text()));
            obj.getNames().add(cols.get(3).text());
            obj.getIds().put("vsx", cols.get(2).text().trim());
            result.add(obj);
        }
        return result;
    }

    private Connection getTemplate() {
        return Jsoup.connect(url)
                .data("-to", "4")
                .data("-from", "-3")
                .data("-this", "-3")
                .data("source", "B/vsx/vsx")
                .data("tables", "B/vsx/vsx")
                .data("-out.max", "50")
                .data("CDSportal", "http://cdsportal.u-strasbg.fr/StoreVizierData.html")
                .data("-out.form", "HTML Table")
                .data("-out.add", "_r")
                .data("outaddvalue", "default")
                .data("-sort", "_r")
                .data("-oc.form", "dec")
                .data("-c.eq", "J2000")
                .data("-c.u", "deg")
                .data("-c.geom", "r")
                .data("-source", "B/vsx/vsx")
                .data("-order", "I")
                .data("-out", "OID")
                .data("-out", "Name")
                .data("-out", "Epoch")
                .data("-out", "Period")
                .data("-out", "RAJ2000")
                .data("-out", "DEJ2000")
                .data("noneucd1p", "on")
                .data("-file", ".")
                .data("-meta.ucd", "2")
                .data("-meta", "1")
                .data("-meta.foot", "1")
                .data("-usenav", "0")
                .data("-bmark", "POST");
    }
}
