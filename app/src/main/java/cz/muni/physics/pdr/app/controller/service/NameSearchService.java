package cz.muni.physics.pdr.app.controller.service;

import cz.muni.physics.pdr.backend.entity.StellarObject;
import cz.muni.physics.pdr.backend.exception.ResourceAvailabilityException;
import cz.muni.physics.pdr.backend.resolver.sesame.SesameNameResolver;
import cz.muni.physics.pdr.backend.resolver.vsx.VSXStarResolver;
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
    private VSXStarResolver vsxStarResolver;

    @Autowired
    public NameSearchService(SesameNameResolver sesameNameResolver,
                             VSXStarResolver vsxStarResolver) {
        this.sesameNameResolver = sesameNameResolver;
        this.vsxStarResolver = vsxStarResolver;
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
                List<StellarObject> vsxResult = vsxStarResolver.findByName(searchText);
                if (vsxResult.size() == 1) {
                    sesameResult.merge(vsxResult.get(0));
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
