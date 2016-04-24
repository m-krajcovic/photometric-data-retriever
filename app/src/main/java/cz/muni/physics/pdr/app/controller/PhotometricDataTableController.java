package cz.muni.physics.pdr.app.controller;

import cz.muni.physics.pdr.app.model.PhotometricDataModel;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;

/**
 * @author Michal Krajčovič
 * @version 1.0
 * @since 24/04/16
 */
public class PhotometricDataTableController {

    @FXML
    private TableColumn<PhotometricDataModel, Number> julianDate;
    @FXML
    private TableColumn<PhotometricDataModel, Number> magnitude;
    @FXML
    private TableColumn<PhotometricDataModel, Number> error;

    @FXML
    private void initialize() {
        julianDate.setCellValueFactory(param -> param.getValue().julianDateProperty());
        magnitude.setCellValueFactory(param -> param.getValue().magnitudeProperty());
        error.setCellValueFactory(param -> param.getValue().errorProperty());

    }
}
