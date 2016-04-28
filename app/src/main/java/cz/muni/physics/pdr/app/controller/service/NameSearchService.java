package cz.muni.physics.pdr.app.controller.service;

import cz.muni.physics.pdr.backend.entity.StellarObject;
import cz.muni.physics.pdr.backend.entity.VizierQuery;
import cz.muni.physics.pdr.backend.entity.VizierResult;
import cz.muni.physics.pdr.backend.exception.ResourceAvailabilityException;
import cz.muni.physics.pdr.backend.resolver.sesame.SesameNameResolver;
import cz.muni.physics.pdr.backend.resolver.vizier.VizierResolver;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.Executor;

/**
 * @author Michal Krajčovič
 * @version 1.0
 * @since 06/04/16
 */
@Component
public class NameSearchService extends Service<StellarObject> {

    private final static Logger logger = LogManager.getLogger(NameSearchService.class);

    private String searchText;

    private SesameNameResolver sesameNameResolver;
    private VizierResolver vsxVizierResolver;

    @Autowired
    public NameSearchService(SesameNameResolver sesameNameResolver,
                             VizierResolver vsxVizierResolver) {
        this.sesameNameResolver = sesameNameResolver;
        this.vsxVizierResolver = vsxVizierResolver;
    }

    @Override
    protected Task<StellarObject> createTask() {
        if (searchText == null) {
            throw new IllegalArgumentException("searchText cannot be null.");
        }
        return new Task<StellarObject>() {
            @Override
            protected StellarObject call() throws ResourceAvailabilityException {
                logger.debug("Trying to get StellarObject.");
                StellarObject sesameResult = sesameNameResolver.findByName(searchText);
                List<VizierResult> vsxResult = vsxVizierResolver.findByQuery(new VizierQuery(searchText));
                if (vsxResult.size() == 1) {
                    sesameResult.getNames().add(vsxResult.get(0).getName());
                    sesameResult.setEpoch(vsxResult.get(0).getEpoch());
                    sesameResult.setPeriod(vsxResult.get(0).getPeriod());
                }
                return sesameResult;
            }
        };
    }

    @Autowired
    public void setTaskExecutor(Executor executor) {
        super.setExecutor(executor);
    }

    public String getSearchText() {
        return searchText;
    }

    public void setSearchText(String searchText) {
        this.searchText = searchText;
    }
}
