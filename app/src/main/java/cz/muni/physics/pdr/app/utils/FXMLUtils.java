package cz.muni.physics.pdr.app.utils;

import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.*;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.Optional;

/**
 * @author Michal Krajčovič
 * @version 1.0
 * @since 30/03/16
 */
public class FXMLUtils {

    public static Stage createDialogStage(String title, Pane pane, Stage parent) {
        Stage dialogStage = new Stage();
        dialogStage.setTitle(title);
        dialogStage.initModality(Modality.WINDOW_MODAL);
        dialogStage.initOwner(parent);
        Scene scene = new Scene(pane);
        dialogStage.setScene(scene);
        dialogStage.showAndWait();
        return dialogStage;
    }

    public static void showTooltip(String text, Window window, Control control) {
        Tooltip tooltip = new Tooltip(text);
        Point2D point = control.localToScene(0.0, 0.0);
        tooltip.setAutoHide(true);
        tooltip.show(window,
                control.getWidth() / 2 - text.length() * 2.9 + control.getScene().getX() + window.getX() + point.getX(),
                control.getHeight() - 5 + control.getScene().getY() + window.getY() + point.getY());
    }

    public static Alert alert(String title, String header, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        return alert;
    }

    public static File showDirChooser(String title, String initPath, Window dialogStage) {
        DirectoryChooser fileChooser = new DirectoryChooser();
        fileChooser.setTitle(title);
        fileChooser.setInitialDirectory(new File(initPath));
        return fileChooser.showDialog(dialogStage);
    }

    public static File showSaveFileChooser(String title,
                                           String initPath,
                                           String initFileName,
                                           Window dialogStage,
                                           FileChooser.ExtensionFilter... extensionFilters) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(title);
        fileChooser.setInitialFileName(initFileName);
        fileChooser.setInitialDirectory(new File(initPath));
        fileChooser.getExtensionFilters().addAll(extensionFilters);
        return fileChooser.showSaveDialog(dialogStage);
    }

    public static void showExceptionAlert(String title, String header, String content, Throwable exc) {
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

    public static Dialog getProgressDialog(Window owner, Task task) {
        Dialog dialog = new Dialog();
        dialog.initStyle(StageStyle.UNDECORATED);
        dialog.initOwner(owner);
        ProgressBar progressBar = new ProgressBar();
        progressBar.setPrefSize(300, 30);
        Label progressLabel = new Label();
        progressLabel.setPrefWidth(300);
        progressLabel.setAlignment(Pos.BASELINE_CENTER);
        VBox vBox = new VBox(progressBar, progressLabel);
        vBox.setFillWidth(true);
        vBox.setPadding(new Insets(10, 10, 10, 10));
        dialog.getDialogPane().setContent(vBox);
        progressBar.progressProperty().bind(task.progressProperty());
        progressLabel.textProperty().bind(task.messageProperty());
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        Node closeButton = dialog.getDialogPane().lookupButton(ButtonType.CLOSE);
        closeButton.managedProperty().bind(closeButton.visibleProperty());
        closeButton.setVisible(false);
        task.setOnSucceeded(event -> dialog.close());
        return dialog;
    }

    public static Optional<String> showOptionDialog(Window owner, List<String> choices,
                                                    String title,
                                                    String header,
                                                    String content){
        ChoiceDialog<String> dialog = new ChoiceDialog<>(choices.get(0), choices);
        dialog.setTitle(title);
        dialog.setHeaderText(header);
        dialog.setContentText(content);
        return dialog.showAndWait();
    }
}
