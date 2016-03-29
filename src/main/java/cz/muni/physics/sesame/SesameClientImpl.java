package cz.muni.physics.sesame;

import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestOperations;
import org.springframework.xml.xpath.XPathOperations;

import javax.xml.transform.Source;
import java.util.stream.Collectors;

/**
 * @author Michal Krajčovič
 * @version 1.0
 * @since 24/03/16
 */
public class SesameClientImpl implements SesameClient {
    private final RestOperations restTemplate;

    private final XPathOperations xpathTemplate;

    public SesameClientImpl(RestOperations restTemplate, XPathOperations xpathTemplate) {
        this.restTemplate = restTemplate;
        this.xpathTemplate = xpathTemplate;
    }

    public SesameResult getAliases(String name) throws ResourceAccessException{
        String sesameUrl = "http://cdsweb.u-strasbg.fr/cgi-bin/nph-sesame/-oIX/A?{name}";

        Source aliases = restTemplate.getForObject(sesameUrl, Source.class, name);
        System.out.println(restTemplate.getForObject(sesameUrl, String.class, name));
        SesameResult result = new SesameResult();
        result.setNames(xpathTemplate.evaluate("//alias | //oname", aliases, (node, i) -> {
            return node.getTextContent();
        }).stream().distinct().collect(Collectors.toList()));


        return result;
    }

    @Override
    public boolean isAvailable() {
        return false;
    }
}
