package cz.muni.physics.pdr.backend.resolver.vizier;

import cz.muni.physics.pdr.backend.entity.VizierQuery;
import cz.muni.physics.pdr.backend.entity.VizierResult;
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
 * Created by Michal on 28-Apr-16.
 */
public class VizierResolverImpl implements VizierResolver {

    private String url;
    private String catalogue;

    public VizierResolverImpl(String url, String catalogue) {
        this.url = url;
        this.catalogue = catalogue;
    }

    public List<VizierResult> findByQuery(VizierQuery query) {
        List<VizierResult> result = new ArrayList<>();
        try {
            Connection con = getTemplate()
                    .data("-c", query.getQuery());
            if (query.getRadius() != null) {
                con.data("-c.r", Double.toString(query.getRadius().getRadius()));
                con.data("-c.u", query.getRadius().getUnit().toString());
            }
            Document doc = con.post();
            result.addAll(parseDoc(doc));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public boolean isAvailable() {
        return true;
    }

    protected List<VizierResult> parseDoc(Document doc) {
        List<VizierResult> result = new ArrayList<>();
        Elements objects = doc.getElementsByClass("tuple-2");
        for (Element object : objects) {
            VizierResult obj = new VizierResult();
            Elements cols = object.getElementsByTag("td");
            obj.setName(cols.get(3).text());
            obj.setDistance(Double.parseDouble(cols.get(1).text()));
            if (NumberUtils.isParsable(cols.get(4).text()))
                obj.setEpoch(Double.parseDouble(cols.get(4).text()));
            if (NumberUtils.isParsable(cols.get(5).text()))
                obj.setPeriod(Double.parseDouble(cols.get(5).text()));
            obj.setRightAscension(cols.get(6).text());
            obj.setDeclination(cols.get(7).text());
            result.add(obj);
        }
        return result;
    }

    protected Connection getTemplate() {
        return Jsoup.connect(url)
                .data("-to", "4")
                .data("-from", "-3")
                .data("-this", "-3")
                .data("source", catalogue)
                .data("tables", catalogue)
                .data("-source", catalogue)
                .data("-out.max", "50")
                .data("CDSportal", "http://cdsportal.u-strasbg.fr/StoreVizierData.html")
                .data("-out.form", "HTML Table")
                .data("-out.add", "_r")
                .data("outaddvalue", "default")
                .data("-sort", "_r")
                .data("-oc.form", "dec")
                .data("-c.eq", "J2000")
                .data("-c.geom", "r")
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
