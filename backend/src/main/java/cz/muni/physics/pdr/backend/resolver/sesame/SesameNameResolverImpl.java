package cz.muni.physics.pdr.backend.resolver.sesame;

import cz.muni.physics.pdr.backend.entity.StellarObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.client.RestOperations;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.IOException;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Michal Krajčovič
 * @version 1.0
 * @since 24/03/16
 */
public class SesameNameResolverImpl implements SesameNameResolver {
    private final static Logger logger = LogManager.getLogger(SesameNameResolverImpl.class);

    private RestOperations restTemplate;
    private String resolverUrl;
    private String testUrl;

    public SesameNameResolverImpl(RestOperations restTemplate,
                                  String resolverUrl,
                                  String testUrl) {
        this.restTemplate = restTemplate;
        this.resolverUrl = resolverUrl;
        this.testUrl = testUrl;
    }

    @Override
    public StellarObject findByName(String name) {
        logger.debug("Resolving name by Sesame with {}", name);
        String response = restTemplate.getForObject(resolverUrl, String.class, name);
        InputSource source = new InputSource(new StringReader(response));
        XPathFactory xpathFactory = XPathFactory.newInstance();
        XPath xpath = xpathFactory.newXPath();
        StellarObject result = new StellarObject();
        Document doc;
        try {
            doc = (Document) xpath.evaluate("/", source, XPathConstants.NODE);
            NodeList list = (NodeList) xpath.evaluate("//alias", doc, XPathConstants.NODESET);
            Set<String> names = new HashSet<>(list.getLength());
            for (int i = 0; i < list.getLength(); i++) {
                Node node = list.item(i);
                names.add(node.getTextContent());
            }
            result.setNames(names);
            String oName = xpath.evaluate("//oname[1]", doc);
            if (!oName.isEmpty()) {
                result.setoName(oName);
            }
            String jradeg = xpath.evaluate("//jradeg[1]", doc);
            if (!jradeg.isEmpty()) {
                result.setRightAscension(Double.parseDouble(jradeg));
            }
            String jdedeg = xpath.evaluate("//jdedeg[1]", doc);
            if (!jdedeg.isEmpty()) {
                result.setDeclination(Double.parseDouble(jdedeg));
            }
        } catch (XPathExpressionException e) {
            throw new RuntimeException("Could not evaluate xpath", e);
        }
        logger.debug("Finished Sesame Name Resolver.");
        return result;
    }

    @Override
    public boolean isAvailable() {
        try {
            final URL url = new URL(testUrl);
            final URLConnection conn = url.openConnection();
            conn.setConnectTimeout(2000);
            conn.connect();
            return true;
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            return false;
        }
    }
}
