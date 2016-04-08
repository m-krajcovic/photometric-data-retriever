package cz.muni.physics.sesame;

import org.springframework.web.client.ResourceAccessException;
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
import java.util.List;

/**
 * @author Michal Krajčovič
 * @version 1.0
 * @since 24/03/16
 */
public class SesameClientImpl implements SesameClient {

    private RestOperations restTemplate;
    private String resolverUrl;
    private String testUrl;

    public SesameClientImpl(RestOperations restTemplate, String resolverUrl, String testUrl) {
        this.restTemplate = restTemplate;
        this.resolverUrl = resolverUrl;
        this.testUrl = testUrl;
    }

    public SesameResult getData(String name) throws XPathExpressionException, ResourceAccessException {
        String response = restTemplate.getForObject(resolverUrl, String.class, name); // TODO try catch something
        InputSource source = new InputSource(new StringReader(response));
        XPathFactory xpathFactory = XPathFactory.newInstance();
        XPath xpath = xpathFactory.newXPath();
        SesameResult result = new SesameResult();
        Document doc = (Document) xpath.evaluate("/", source, XPathConstants.NODE);
        NodeList list= (NodeList) xpath.evaluate("(//alias | //oname)", doc, XPathConstants.NODESET);
        List<String> names = new ArrayList<>(list.getLength());
        for (int i = 0; i < list.getLength(); i++) {
            Node node = list.item(i);
            names.add(node.getTextContent());
        }
        result.setNames(names);
        result.setJpos(xpath.evaluate("//jpos[1]", doc)); // TODO check these somehow
        result.setJraddeg(xpath.evaluate("//jradeg[1]", doc));
        result.setJdedeg(xpath.evaluate("//jdedeg[1]", doc));
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
