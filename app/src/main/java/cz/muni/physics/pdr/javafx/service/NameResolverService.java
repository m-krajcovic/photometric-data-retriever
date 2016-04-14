package cz.muni.physics.pdr.javafx.service;

import cz.muni.physics.pdr.resolver.StarName;
import cz.muni.physics.pdr.resolver.StarResolverManager;
import cz.muni.physics.pdr.resolver.StarResolverResult;
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
public class NameResolverService extends Service<StarResolverResult> {

    private String searchText;

    @Autowired
    private StarResolverManager<StarName> nameResolverManager;

    @Override
    protected Task<StarResolverResult> createTask() {
        if (searchText == null) {
            throw new IllegalArgumentException("searchText cannot be null.");
        }
        return new Task<StarResolverResult>() {

            @Override
            protected StarResolverResult call() {
                return nameResolverManager.resolveFor(new StarName(searchText));
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
