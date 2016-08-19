package cz.muni.physics.pdr.backend.resolver.vizier;

import cz.muni.physics.pdr.backend.entity.VizierQuery;
import cz.muni.physics.pdr.backend.entity.VizierResult;
import cz.muni.physics.pdr.backend.utils.NumberUtils;
import cz.muni.physics.pdr.vizier.VizierHtmlParser;
import cz.muni.physics.pdr.vizier.VizierService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Connection;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Michal on 28-Apr-16.
 */
public class VizierResolverTsvImpl implements VizierResolver {
    private static final Logger logger = LogManager.getLogger(VizierResolverTsvImpl.class);

    private String url;
    private String catalogue;
    private VizierService vizierService;

    /**
     * Creates Resolver for Vizier with given catalogue and vizier url
     *
     * @param url       vizier url
     * @param catalogue vizier catalogue
     */
    public VizierResolverTsvImpl(String url, String catalogue) {
        this.url = url;
        this.catalogue = catalogue;
        this.vizierService = new VizierService();
    }

    public List<VizierResult> findByQuery(VizierQuery query) {
        List<VizierResult> result = new ArrayList<>();
        try {
            Connection con = getTemplate()
                    .data("-c", query.getQuery());
            if (query.getRadius() != null && query.getRadius().getRadius() != 0) {
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
        logger.debug("Checking availability of Vizier Service");
        try {
            final URL url = new URL(this.url);
            final URLConnection conn = url.openConnection();
            conn.setConnectTimeout(2000);
            conn.connect();
            logger.debug("Service is available");
            return true;
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            logger.debug("Service is unavailable");
            return false;
        }
    }

    @Override
    public String getServiceName() {
        return "Vizier";
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
                    if (NumberUtils.isParsable(row[5]))
                        obj.setRightAscension(Double.parseDouble(row[5]));
                    if (NumberUtils.isParsable(row[6]))
                        obj.setDeclination(Double.parseDouble(row[6]));
                    result.add(obj);
                }
            }
        }
        return result;
    }

    protected Connection getTemplate() {
        return vizierService.getConnectionFromVizier(catalogue, VizierService.OutputType.TSV)
                .data("-out", "OID")
                .data("-out", "Name")
                .data("-out", "Epoch")
                .data("-out", "Period")
                .data("-out", "_RAJ,_DEJ");
    }

    public static void main(String[] args) {
        VizierResolverTsvImpl resolver = new VizierVSXStarResolver();
        for (VizierResult vizierResult : resolver.findByQuery(new VizierQuery("RW Com"))) {
            System.out.println(vizierResult.toString());
        }
        //VizierResult{name='RW Com', distance=7.0E-4, epoch=2454918.704, period=0.23734706, rightAscension=188.25117, declination=26.71622}

        VizierService vizierService = new VizierService();
        VizierHtmlParser parser = new VizierHtmlParser();

        try {
            Document parse = vizierService.getConnectionFromVizier("J/AJ/134/1963/EBs", VizierService.OutputType.HTML)
                    .data("LC", "LC")
                    .data("-out", "MACHO")
                    .data("-out", "LC")
                    .data("-c", "MACHO 208.16083.86").method(Connection.Method.POST).execute().parse();
            List<Map<String, String>> results = parser.getResults(parse);
            for (Map<String, String> result : results) {
                result.forEach((s, s2) -> {
                    System.out.println(s + ": " + s2);
                });
                System.out.println("---------------------------------------------");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
