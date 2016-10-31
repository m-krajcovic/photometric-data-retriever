package cz.muni.physics.pdr.app.controller;

import cz.muni.physics.pdr.app.spring.Screens;
import cz.muni.physics.pdr.app.utils.FXMLUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.util.ResourceBundle;

/**
 * @author Michal
 * @version 1.0
 * @since 7/15/2016
 */
@Component
public class AboutOverviewController extends StageController {

    private static final Logger logger = LogManager.getLogger(AboutOverviewController.class);

    @Autowired
    private Screens app;
    @Value("${version}")
    private String version;
    @Value("${github.project.url}")
    private String githubUrl;

    @FXML
    private ResourceBundle resources;

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
        if (Desktop.isDesktopSupported()) {
            try {
                Desktop.getDesktop().browse(URI.create(githubUrl));
            } catch (IOException e) {
                logger.error("Failed to open browser with url " + githubUrl, e);
                FXMLUtils.alert("Error!", "Failed to open browser", "", Alert.AlertType.ERROR).showAndWait();
            }
        } else {
            FXMLUtils.alert(resources.getString("not.supported"), resources.getString("desktop.open.not.supported"),"", Alert.AlertType.ERROR).showAndWait();
        }
    }
}
