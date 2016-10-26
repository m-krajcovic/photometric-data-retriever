package cz.muni.physics.pdr.backend.resolver.simbad;

import cz.muni.physics.pdr.backend.entity.Radius;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Michal
 * @version 1.0
 * @since 8/10/2016
 */
public class SimbadResolverImpl implements SimbadResolver {
    private static final Logger logger = LogManager.getLogger(SimbadResolverImpl.class);
    private String coordsUrl = "http://simbad.u-strasbg.fr/simbad/sim-coo";
    private String identUrl = "http://simbad.u-strasbg.fr/simbad/sim-id";

    //http://simbad.u-strasbg.fr/simbad/sim-coo?output.format=ascii&output.format=ASCII&Coord=188.2510315%2026.7161768&Radius=10&Radius.unit=arcmin


    public boolean isAvailable() {
        logger.debug("Checking availability of Simbad Service");
        try {
            final URL url = new URL(this.coordsUrl);
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
        return "Simbad";
    }

    @Override
    public List<SimbadResult> findByCoords(String query, Radius radius) {
        List<SimbadResult> result = new ArrayList<>();
        try (InputStream is = UriComponentsBuilder.fromHttpUrl(coordsUrl).queryParam("output.format", "ASCII")
                .queryParam("output.max", "100")
                .queryParam("Coord", query)
                .queryParam("Radius", Double.toString(radius.getRadius()))
                .queryParam("Radius.unit", radius.getUnit().toString()).build().toUri().toURL().openStream();
             BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
            String line;
            for (int i = 0; i < 9; i++) {
                reader.readLine();
            }
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty() && !line.startsWith("=")) {
                    String[] cols = line.split("\\|");
                    if (cols.length > 4) {
                        SimbadResult currentObject = new SimbadResult();
                        currentObject.setDistance(Radius.Unit.ARC_SEC.convertTo(Double.parseDouble(cols[1].trim()), radius.getUnit()));
                        currentObject.setIdentifier(cols[2].trim());
                        String[] coords = cols[4].trim().split("(?=[-+])");
                        currentObject.setRightAscension(coords[0]);
                        currentObject.setDeclination(coords[1]);
                        result.add(currentObject);
                    }
                }
            }
        } catch (IOException e) {
            logger.error(e);
        }
        return result;
    }

    @Override
    public SimbadResult findByIdentifier(String query) {
        return null;
    }

    public static void main(String[] args) {
        SimbadResolver simbadResolver = new SimbadResolverImpl();
        simbadResolver.findByCoords("188.2510315 26.7161768", new Radius(10, Radius.Unit.ARC_MIN));
    }
}
