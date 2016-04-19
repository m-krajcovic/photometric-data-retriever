package cz.muni.physics.pdr.app.javafx;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.text.Text;

import java.util.Map;
import java.util.function.UnaryOperator;

/**
 * @author Michal Krajčovič
 * @version 1.0
 * @since 19/04/16
 */
public class TitledTextField extends TextField {

    private TextField titleTextField;

    public TitledTextField() {
        this(false);
    }

    public TitledTextField(boolean visibleTitle) {
        super();
        titleTextField = new TextField();
        titleTextField.getStyleClass().add("no-right-radius");
        titleTextField.setDisable(true);
        titleTextField.setFocusTraversable(false);
        titleTextField.setEditable(false);
        titleTextField.setVisible(visibleTitle);
        titleTextField.setMinWidth(Region.USE_PREF_SIZE);
        titleTextField.setMaxWidth(Region.USE_PREF_SIZE);
        titleTextField.textProperty().addListener((ov, prevText, currText) -> {
            Platform.runLater(() -> {
                Text text = new Text(currText);
                text.setFont(titleTextField.getFont());
                double width = text.getLayoutBounds().getWidth()
                        + titleTextField.getPadding().getLeft() + titleTextField.getPadding().getRight()
                        + 2d;
                titleTextField.setPrefWidth(width);
                titleTextField.positionCaret(titleTextField.getCaretPosition());
                if (!currText.isEmpty()) {
                    setPadding(new Insets(5, 8, 5, 5 + titleTextField.getPrefWidth()));
                } else {
                    setPadding(new Insets(5, 8, 5, 8));
                }
            });
        });
        getChildren().add(titleTextField);

        setOnKeyPressed(event -> {
            if (event.getCode().equals(KeyCode.ESCAPE)) {
                hideTitle();
            } else if (event.getCode().equals(KeyCode.BACK_SPACE)) {
                if (getText().isEmpty()) {
                    hideTitle();
                }
            }
        });

    }

    public void setAutomaticTitles(String delimiter, Map<String, String> automaticTitles) {
        UnaryOperator<TextFormatter.Change> titlesChange = new UnaryOperator<TextFormatter.Change>() {
            @Override
            public TextFormatter.Change apply(TextFormatter.Change change) {
                if (change.getText().equals(delimiter)) {
                    for (Map.Entry<String, String> entry : automaticTitles.entrySet()) {
                        if (change.getControlText().equalsIgnoreCase(entry.getKey())) {
                            TitledTextField.this.showTitle(entry.getValue());
                            TitledTextField.this.setText("");
                            return null;
                        }
                    }
                }
                return change;
            }
        };
        setTextFormatter(new TextFormatter<>(titlesChange));
    }

    public String getTextWithPrefix() {
        return titleTextField.getText() + super.getText();
    }

    public void hideTitle() {
        titleTextField.setText("");
        titleTextField.setVisible(false);
    }

    public void showTitle(String text) {
        titleTextField.setText(text);
        titleTextField.setVisible(true);
    }

    public void setPrimaryPane(Pane primaryPane) {
        primaryPane.getChildren().add(titleTextField);
        Point2D point = localToScene(getLayoutX(), getLayoutY());
        titleTextField.setLayoutX(point.getX());
        titleTextField.setLayoutY(point.getY());
    }

}
