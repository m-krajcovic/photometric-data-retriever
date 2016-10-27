package cz.muni.physics.pdr.app.controller;

import cz.muni.physics.pdr.app.spring.Screens;
import javafx.fxml.FXML;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author Michal
 * @version 1.0
 * @since 7/15/2016
 */
@Component
public class AboutOverviewController extends StageController {

    @Autowired
    private Screens app;
    @Value("${version}")
    private String version;

    @FXML
    private ImageView imageView;
    @FXML
    private Label topLabel;
    @FXML
    private Label bottomLabel;
    @FXML
    private Hyperlink topHyperlink;

    @FXML
    private void initialize() {
        imageView.setImage(new Image(this.getClass().getResourceAsStream("/images/planet.png")));
        topLabel.setText("Photometric Data Retriever");
        bottomLabel.setText("Version " + version);
        topHyperlink.setText("GitHub Project");
    }


    @FXML
    private void onTopHyperlinkClick() {
        app.getHostServices().showDocument("https://github.com/m-krajcovic/photometric-data-retriever");
    }
}
