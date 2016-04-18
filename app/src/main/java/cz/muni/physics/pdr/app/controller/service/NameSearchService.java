package cz.muni.physics.pdr.app.controller.service;

import cz.muni.physics.pdr.backend.entity.StellarObject;
import cz.muni.physics.pdr.backend.entity.StellarObjectName;
import cz.muni.physics.pdr.backend.resolver.StarResolverManager;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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

    private StarResolverManager<StellarObjectName> nameResolverManager;

    @Autowired
    public NameSearchService(StarResolverManager<StellarObjectName> nameResolverManager,
                             Executor executor) {
        this.nameResolverManager = nameResolverManager;
        super.setExecutor(executor);
    }

    @Override
    protected Task<StellarObject> createTask() {
        if (searchText == null) {
            throw new IllegalArgumentException("searchText cannot be null.");
        }
        return new Task<StellarObject>() {

            @Override
            protected StellarObject call() {
                logger.debug("Trying to get StellarObject.");
                return nameResolverManager.resolverForResult(new StellarObjectName(searchText));
            }
        };
    }

    public String getSearchText() {
        return searchText;
    }

    public void setSearchText(String searchText) {
        this.searchText = searchText;
    }
}
