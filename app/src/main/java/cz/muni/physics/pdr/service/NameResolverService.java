package cz.muni.physics.pdr.service;

import cz.muni.physics.pdr.nameresolver.NameResolverManager;
import cz.muni.physics.pdr.nameresolver.NameResolverResult;
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
        if (searchText == null) {
            throw new IllegalArgumentException("searchText cannot be null.");
        }
        return new Task<NameResolverResult>() {

            @Override
            protected NameResolverResult call() {
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
