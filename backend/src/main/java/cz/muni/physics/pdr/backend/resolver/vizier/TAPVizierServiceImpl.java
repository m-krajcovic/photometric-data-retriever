package cz.muni.physics.pdr.backend.resolver.vizier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.client.RestOperations;

public class TAPVizierServiceImpl implements TAPVizierService {
    private static final Logger logger = LogManager.getLogger(TAPVizierServiceImpl.class);

    private RestOperations restTemplate;

    public TAPVizierServiceImpl(RestOperations restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public TAPVizierResult query(String query) {
        logger.debug("Tapping query " + query);
        TAPVizierResult forObject = restTemplate.getForObject("http://tapvizier.u-strasbg.fr/TAPVizieR/tap/sync?request=doQuery&lang=adql&format=json&query={query}", TAPVizierResult.class, query);
        return forObject;
    }
}
