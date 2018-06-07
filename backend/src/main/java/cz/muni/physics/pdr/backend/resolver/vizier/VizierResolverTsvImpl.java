package cz.muni.physics.pdr.backend.resolver.vizier;

import cz.muni.physics.pdr.backend.entity.VizierQuery;
import cz.muni.physics.pdr.backend.entity.VizierResult;
import cz.muni.physics.pdr.backend.utils.NumberUtils;
import cz.muni.physics.pdr.vizier.VizierService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Connection;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;

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
        Set<VizierResult> result = new HashSet<>();
        try {
            Connection con = getTemplate()
                    .data("-c", query.getQuery());
            if (query.getRadius() != null && query.getRadius().getRadius() != 0) {
                con.data("-c.r", Double.toString(query.getRadius().getRadius()));
                con.data("-c.u", query.getRadius().getUnit().toString());
            }
            result.addAll(parseDoc(con.method(Connection.Method.POST).execute().body()));
            if (result.isEmpty()) {
                con = getTemplate()
                        .data("-c", "")
                        .data("Name", query.getQuery());
                if (query.getRadius() != null && query.getRadius().getRadius() != 0) {
                    con.data("-c.r", Double.toString(query.getRadius().getRadius()));
                    con.data("-c.u", query.getRadius().getUnit().toString());
                }
                result.addAll(parseDoc(con.method(Connection.Method.POST).execute().body()));
            }
        } catch (IOException e) {
            logger.error(e);
        }
        ArrayList<VizierResult> vizierResults = new ArrayList<>(result);
        vizierResults.sort(Comparator.comparingDouble(VizierResult::getDistance));
        return vizierResults;
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
            logger.error("Service is unavailable", e);
            return false;
        }
    }

    @Override
    public String getServiceName() {
        return "Vizier";
    }

    protected List<VizierResult> parseDoc(String string) {
        List<VizierResult> result = new ArrayList<>();
        Arrays.stream(string.split("\n")).filter(s -> !s.startsWith("#") && !s.isEmpty()).skip(3).forEach(s -> {
            String[] row = s.split(";");
            for (int i = 0; i < row.length; i++) {
                row[i] = row[i].trim();
            }
            int offset = -1;
            VizierResult obj = new VizierResult();
            if (row.length >= 7) {
                offset = 0;
                if (NumberUtils.isParsable(row[0])) {
                    obj.setDistance(Double.parseDouble(row[0]));
                }
            }
            obj.setName(row[2 + offset]);
            if (NumberUtils.isParsable(row[3 + offset]))
                obj.setEpoch(Double.parseDouble(row[3 + offset]));
            if (NumberUtils.isParsable(row[4 + offset]))
                obj.setPeriod(Double.parseDouble(row[4 + offset]));
            if (NumberUtils.isParsable(row[5 + offset]))
                obj.setRightAscension(Double.parseDouble(row[5 + offset]));
            if (NumberUtils.isParsable(row[6 + offset]))
                obj.setDeclination(Double.parseDouble(row[6 + offset]));
            result.add(obj);
        });
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
}
