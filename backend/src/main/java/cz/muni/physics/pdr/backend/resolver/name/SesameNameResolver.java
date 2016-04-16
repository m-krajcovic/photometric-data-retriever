package cz.muni.physics.pdr.backend.resolver.name;

import cz.muni.physics.pdr.backend.entity.StellarObjectName;
import cz.muni.physics.pdr.backend.resolver.StarResolver;
import cz.muni.physics.pdr.backend.entity.StellarObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Michal Krajčovič
 * @version 1.0
 * @since 24/03/16
 */
@Component
public class SesameNameResolver implements StarResolver<StellarObjectName> {

    private RestOperations restTemplate;
    private String resolverUrl;
    private String testUrl;

    @Autowired
    public SesameNameResolver(RestOperations restTemplate,
                              @Value("${sesame.resolver.url}") String resolverUrl,
                              @Value("${sesame.test.url}") String testUrl) {
        this.restTemplate = restTemplate;
        this.resolverUrl = resolverUrl;
        this.testUrl = testUrl;
    }

    @Override
    public List<StellarObject> getResults(StellarObjectName param) {
        return Collections.singletonList(getResult(param));
    }

    public StellarObject getResult(StellarObjectName name) {
        String response = restTemplate.getForObject(resolverUrl, String.class, name.getValue()); // TODO try catch something
        InputSource source = new InputSource(new StringReader(response));
        XPathFactory xpathFactory = XPathFactory.newInstance();
        XPath xpath = xpathFactory.newXPath();
        StellarObject result = new StellarObject();
        Document doc;
        try {
            doc = (Document) xpath.evaluate("/", source, XPathConstants.NODE);

            NodeList list = (NodeList) xpath.evaluate("(//alias | //oname)", doc, XPathConstants.NODESET);
            List<String> names = new ArrayList<>(list.getLength());
            for (int i = 0; i < list.getLength(); i++) {
                Node node = list.item(i);
                names.add(node.getTextContent());
            }
            result.setNames(names);
            result.setJpos(xpath.evaluate("//jpos[1]", doc)); // TODO check these somehow
            result.setRightAscension(Double.parseDouble(xpath.evaluate("//jradeg[1]", doc)));
            result.setDeclination(Double.parseDouble(xpath.evaluate("//jdedeg[1]", doc)));
        } catch (XPathExpressionException e) {
            e.printStackTrace();
        }
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
