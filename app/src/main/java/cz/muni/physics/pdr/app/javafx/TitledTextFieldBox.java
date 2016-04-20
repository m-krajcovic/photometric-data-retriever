package cz.muni.physics.pdr.app.javafx;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.text.Text;

import java.util.Map;
import java.util.function.UnaryOperator;

/**
 * @author Michal Krajčovič
 * @version 1.0
 * @since 20/04/16
 */
public class TitledTextFieldBox extends HBox {
    private Label title;
    private TextField textField;
    private UnaryOperator<TextFormatter.Change> filter = c -> c;
    private String delimiter;
    private Map<String, String> autoTitles;
    private boolean useAutoTitles = false;

    public TitledTextFieldBox() {
        super();
        title = new Label();
        title.getStyleClass().add("titled-label");
        title.setFocusTraversable(false);
        title.setVisible(false);
        title.setMinWidth(Region.USE_PREF_SIZE);
        title.setMaxWidth(Region.USE_PREF_SIZE);
        textField = new TextField();

        widthProperty().addListener((observable, oldValue, newValue) -> {
            textField.setPrefWidth(newValue.doubleValue());
        });

        heightProperty().addListener((observable, oldValue, newValue) -> {
            textField.setPrefHeight(newValue.doubleValue());
            title.setPrefHeight(newValue.doubleValue());
        });

        title.textProperty().addListener((ov, prevText, currText) -> {
            Platform.runLater(() -> {
                if (currText.isEmpty()) {
                    title.setPrefWidth(0);
                    this.textField.setPrefWidth(getWidth());
                } else {
                    Text text = new Text(currText);
                    text.setFont(title.getFont());
                    double width = text.getLayoutBounds().getWidth()
                            + title.getPadding().getLeft() + title.getPadding().getRight()
                            + 2d;
                    width = Math.ceil(width);
                    title.setPrefWidth(width);
                    this.textField.setPrefWidth(getWidth() - width);
                }
            });
        });

        textField.setOnKeyPressed(event -> {
            if (event.getCode().equals(KeyCode.ESCAPE)) {
                hideTitle();
            } else if (event.getCode().equals(KeyCode.BACK_SPACE)) {
                if (textField.getText().isEmpty()) {
                    hideTitle();
                }
            }
        });

        getChildren().addAll(title, textField);
    }

    public void setAutomaticTitles(String delimiter, Map<String, String> automaticTitles) {
        useAutoTitles = true;
        this.delimiter = delimiter;
        this.autoTitles = automaticTitles;
        setTextFormatter(new TextFormatter<>(filter));
    }

    public void setTextFormatter(TextFormatter<?> value) {
        if (value == null) {
            throw new IllegalArgumentException("TextFormatter value cannot be null.");
        }
        filter = value.getFilter();
        textField.setTextFormatter(new TextFormatter<>(change -> {
            if (useAutoTitles && title.getText().isEmpty()) {
                if (change.getText().equals(delimiter)) {
                    for (Map.Entry<String, String> entry : autoTitles.entrySet()) {
                        if (change.getControlText().equalsIgnoreCase(entry.getKey())) {
                            TitledTextFieldBox.this.showTitle(entry.getValue());
                            textField.setText("");
                            return null;
                        }
                    }
                }
            }
            return filter.apply(change);
        }));
    }

    public String getTextWithPrefix() {
        return title.getText() + textField.getText();
    }

    public void hideTitle() {
        title.setText("");
        title.setVisible(false);
        textField.getStyleClass().remove("titled-text-field");
    }

    public void showTitle(String text) {
        title.setText(text);
        title.setVisible(true);
        textField.getStyleClass().add("titled-text-field");
    }

    public Label getTitle() {
        return title;
    }

    public TextField getTextField() {
        return textField;
    }

    public void setOnAction(EventHandler<ActionEvent> onAction) {
        textField.setOnAction(onAction);
    }

    public EventHandler<ActionEvent> getOnAction(){
        return textField.getOnAction();
    }

    public boolean isUseAutoTitles() {
        return useAutoTitles;
    }

    public void setUseAutoTitles(boolean useAutoTitles) {
        this.useAutoTitles = useAutoTitles;
    }
}
