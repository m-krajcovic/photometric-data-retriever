package cz.muni.physics.utils;

import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * @author Michal Krajčovič
 * @version 1.0
 * @since 30/03/16
 */
public class FXMLUtil {

    public static Stage createDialogStage(String title, Pane pane, Stage parent){
        Stage dialogStage = new Stage();
        dialogStage.setTitle(title);
        dialogStage.initModality(Modality.WINDOW_MODAL);
        dialogStage.initOwner(parent);
        Scene scene = new Scene(pane);
        dialogStage.setScene(scene);
        dialogStage.showAndWait();
        return dialogStage;
    }

    public static void showTooltip(String text, Stage stage, Control control){
        Tooltip tooltip = new Tooltip(text);
        Point2D point = control.localToScene(0.0, 0.0);
        tooltip.setAutoHide(true);
        tooltip.show(stage,
                control.getWidth()/2 - text.length()*2.9 + control.getScene().getX() + stage.getX() + point.getX(),
                control.getHeight() - 5 + control.getScene().getY() + stage.getY() + point.getY());
    }

    public static void showAlert(String title, String header, String content, Alert.AlertType type){
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public static void showExceptionAlert(String title, String header, String content, Exception exc){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        exc.printStackTrace(pw);
        String exceptionText = sw.toString();

        Label label = new Label("The exception stacktrace was:");

        TextArea textArea = new TextArea(exceptionText);
        textArea.setEditable(false);
        textArea.setWrapText(true);
        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);

        GridPane expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.add(label, 0, 0);
        expContent.add(textArea, 0, 1);

        alert.getDialogPane().setExpandableContent(expContent);
        alert.showAndWait();
    }
}