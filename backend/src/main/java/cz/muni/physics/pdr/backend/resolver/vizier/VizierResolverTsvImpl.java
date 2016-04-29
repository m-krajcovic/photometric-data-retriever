package cz.muni.physics.pdr.backend.resolver.vizier;

import cz.muni.physics.pdr.backend.entity.VizierQuery;
import cz.muni.physics.pdr.backend.entity.VizierResult;
import org.apache.commons.lang3.math.NumberUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Michal on 28-Apr-16.
 */
public class VizierResolverTsvImpl implements VizierResolver {

    private String url;
    private String catalogue;

    public VizierResolverTsvImpl(String url, String catalogue) {
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
            result.addAll(parseDoc(con.method(Connection.Method.POST).execute().body()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public boolean isAvailable() {
        return true;
    }

    protected List<VizierResult> parseDoc(String string) {
        List<VizierResult> result = new ArrayList<>();
        for (String s : string.split("\n")) {
            if (!s.startsWith("#") && !s.isEmpty()) {
                String[] row = s.split(";");
                for (int i = 0; i < row.length; i++) {
                    row[i] = row[i].trim();
                }
                if (NumberUtils.isParsable(row[0])) {
                    VizierResult obj = new VizierResult();
                    obj.setDistance(Double.parseDouble(row[0]));
                    obj.setName(row[2]);
                    if (NumberUtils.isParsable(row[3]))
                        obj.setEpoch(Double.parseDouble(row[3]));
                    if (NumberUtils.isParsable(row[4]))
                        obj.setPeriod(Double.parseDouble(row[4]));
                    obj.setRightAscension(row[5]);
                    obj.setDeclination(row[6]);
                    result.add(obj);
                }
            }
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
                .data("-out.form", "; -Separated-Values")
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
                .data("-out", "_RAJ,_DEJ")
                .data("noneucd1p", "on")
                .data("-file", ".")
                .data("-meta.ucd", "2")
                .data("-meta", "1")
                .data("-meta.foot", "1")
                .data("-usenav", "0")
                .data("-bmark", "POST");
    }
}
