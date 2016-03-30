package cz.muni.physics.sesame;

import org.springframework.web.client.RestOperations;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Michal Krajčovič
 * @version 1.0
 * @since 24/03/16
 */
public class SesameClientImpl implements SesameClient {
    private final RestOperations restTemplate;


    public SesameClientImpl(RestOperations restTemplate) {
        this.restTemplate = restTemplate;
    }

    public SesameResult getData(String name) throws XPathExpressionException {
        String sesameUrl = "http://cdsweb.u-strasbg.fr/cgi-bin/nph-sesame/-oIX/A?{name}";
        String response = restTemplate.getForObject(sesameUrl, String.class, name);
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
        result.setJpos(xpath.evaluate("//jpos[1]", doc));
        result.setJdedeg(xpath.evaluate("//jradeg[1]", doc));
        result.setJraddeg(xpath.evaluate("//jdedeg[1]", doc));
        System.out.println(result.getJpos());
        return result;
    }

    @Override
    public boolean isAvailable() {
        return false;
    }
}
