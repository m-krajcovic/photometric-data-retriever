package cz.muni.physics.sesame;

import org.springframework.web.client.ResourceAccessException;

import javax.xml.xpath.XPathExpressionException;

/**
 * @author Michal Krajčovič
 * @version 1.0
 * @since 24/03/16
 */
public interface SesameClient {
    SesameResult getData(String name) throws ResourceAccessException, XPathExpressionException;
    boolean isAvailable();
}
