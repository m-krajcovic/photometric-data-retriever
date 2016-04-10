package cz.muni.physics.service;

import cz.muni.physics.nameresolver.NameResolverManager;
import cz.muni.physics.nameresolver.NameResolverResult;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Michal Krajčovič
 * @version 1.0
 * @since 06/04/16
 */
@Component
public class NameResolverService extends Service<NameResolverResult> {

    private String searchText;

    @Autowired
    private NameResolverManager nameResolverManager;

    @Override
    protected Task<NameResolverResult> createTask() {
        return new Task<NameResolverResult>() {

            @Override
            protected NameResolverResult call() throws Exception { //TODO
                return nameResolverManager.resolveFor(searchText);
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
