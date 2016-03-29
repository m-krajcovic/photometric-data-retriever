package cz.muni.physics.utils

import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.layout.GridPane
import javafx.scene.layout.Pane
import javafx.scene.layout.Priority
import javafx.stage.Modality
import javafx.stage.Stage
import org.apache.log4j.Logger
import java.io.FileNotFoundException
import java.io.IOException
import java.io.PrintWriter
import java.io.StringWriter

/**
 * @author Michal Krajčovič
 * @version 1.0
 * @since 20/03/16
 */
object FXMLUtil {

    val logger = Logger.getLogger(FXMLUtil::class.java)

    fun <T : Pane> loadFXML(viewName: String): T? {
        if(!validView(viewName)){
            logger.error("oh shait")
        }
        try {
            var loader = FXMLLoader()
            loader.location = FXMLUtil::class.java.getResource("/view/$viewName.fxml")
            return loader.load()
        } catch(e: IOException) {
            logger.error("oh shait")
            return null;
        }
    }

    fun validView(viewName: String) : Boolean {
        return FXMLUtil::class.java.getResource("/view/$viewName.fxml") != null
    }

    fun createDialogStage(title: String, pane: Pane, parent: Stage) : Stage{
        val dialogStage = Stage()
        dialogStage.title = title
        dialogStage.initModality(Modality.WINDOW_MODAL)
        dialogStage.initOwner(parent)
        val scene = Scene(pane)
        dialogStage.scene = scene
        dialogStage.showAndWait()
        return dialogStage
    }

    fun showTooltip(text: String, stage: Stage, control: Control){
        val tooltip = Tooltip(text);
        val point = control.localToScene(0.0, 0.0)
        tooltip.isAutoHide = true
        tooltip.show(stage,
                control.width/2 - text.length*2.9 + control.scene.x + stage.x + point.x,
                control.height - 5 + control.scene.y + stage.y + point.y)
    }

    fun showAlert(title: String, header:String, content: String, type : Alert.AlertType){
        val alert = Alert(Alert.AlertType.ERROR)
        alert.title = title
        alert.headerText = header
        alert.contentText = content
        alert.showAndWait()
    }

    fun showExceptionAlert(){
        val alert = Alert(Alert.AlertType.ERROR);
        alert.title = "Exception Dialog";
        alert.headerText = "Look, an Exception Dialog";
        alert.contentText = "Could not find file blabla.txt!";

        val ex = FileNotFoundException("Could not find file blabla.txt");

        // Create expandable Exception.
        val sw = StringWriter();
        val pw = PrintWriter(sw);
        ex.printStackTrace(pw);
        val exceptionText = sw.toString();

        val label = Label("The exception stacktrace was:");

        val textArea = TextArea(exceptionText);
        textArea.isEditable = false;
        textArea.isWrapText = true;

        textArea.maxWidth = Double.MAX_VALUE;
        textArea.maxHeight = Double.MAX_VALUE;
        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);

        val expContent = GridPane();
        expContent.maxWidth = Double.MAX_VALUE;
        expContent.add(label, 0, 0);
        expContent.add(textArea, 0, 1);

        // Set expandable Exception into the dialog pane.
        alert.dialogPane.expandableContent = expContent;

        alert.showAndWait();
    }
}
