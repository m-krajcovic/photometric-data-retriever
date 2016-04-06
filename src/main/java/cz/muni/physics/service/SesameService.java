package cz.muni.physics.service;

import cz.muni.physics.sesame.SesameClient;
import cz.muni.physics.sesame.SesameResult;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Michal Krajčovič
 * @version 1.0
 * @since 06/04/16
 */
public class SesameService extends Service<SesameResult> {

    private String searchText;

    @Autowired
    private SesameClient sesameClient;

    @Override
    protected Task<SesameResult> createTask() {
        return new Task<SesameResult>() {

            @Override
            protected SesameResult call() throws Exception {
                return sesameClient.getData(searchText);
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
