package cz.muni.physics.pdr.app.javafx.control;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.text.Text;
import javafx.util.StringConverter;

import java.util.Map;
import java.util.function.UnaryOperator;

/**
 * @author Michal Krajčovič
 * @version 1.0
 * @since 20/04/16
 */
public class TitledTextFieldBox extends HBox {
    private Label titleLabel;
    private TextField textField;
    private UnaryOperator<TextFormatter.Change> filter = c -> c;
    private String delimiter;
    private Map<String, String> autoTitles;
    private boolean useAutoTitles = false;
    private StringProperty textWithPrefix = new SimpleStringProperty("");

    public TitledTextFieldBox() {
        super();
        titleLabel = new Label();
        titleLabel.getStyleClass().add("titled-label");
        titleLabel.setFocusTraversable(false);
        titleLabel.setVisible(false);
        titleLabel.setMinWidth(Region.USE_PREF_SIZE);
        titleLabel.setMaxWidth(Region.USE_PREF_SIZE);
        textField = new TextField();

        widthProperty().addListener((observable, oldValue, newValue) -> {
            textField.setPrefWidth(newValue.doubleValue());
        });

        heightProperty().addListener((observable, oldValue, newValue) -> {
            textField.setPrefHeight(newValue.doubleValue());
            titleLabel.setPrefHeight(newValue.doubleValue());
        });

        titleLabel.textProperty().addListener((ov, prevText, currText) -> {
            Platform.runLater(() -> {
                if (currText.isEmpty()) {
                    titleLabel.setPrefWidth(0);
                    this.textField.setPrefWidth(getWidth());
                    titleLabel.setVisible(false);
                    textField.getStyleClass().remove("titled-text-field");
                } else {
                    Text text = new Text(currText);
                    text.setFont(titleLabel.getFont());
                    double width = text.getLayoutBounds().getWidth()
                            + titleLabel.getPadding().getLeft() + titleLabel.getPadding().getRight()
                            + 2d;
                    width = Math.ceil(width);
                    titleLabel.setPrefWidth(width);
                    this.textField.setPrefWidth(getWidth() - width);
                    titleLabel.setVisible(true);
                    this.textField.getStyleClass().add("titled-text-field");
                }
            });
            updateTextWithPrefix();
        });

        textField.textProperty().bindBidirectional(textWithPrefix, new StringConverter<String>(){
            @Override
            public String toString(String object) {
                return object;
            }

            @Override
            public String fromString(String string) {
                return titleLabel.getText() + textField.getText();

            }
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

        getChildren().addAll(titleLabel, textField);
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
            if (useAutoTitles && titleLabel.getText().isEmpty()) {
                if (change.getControlNewText().contains(delimiter)) {
                    for (Map.Entry<String, String> entry : autoTitles.entrySet()) {
                        if (change.getControlNewText().startsWith(entry.getKey())) {
                            TitledTextFieldBox.this.setTitle(entry.getValue());
                            String newText = change.getControlNewText().substring(entry.getValue().length());
                            textField.setText(newText);
                            return null;
                        }
                    }
                }
            }
            return filter.apply(change);
        }));
    }

    private void updateTextWithPrefix() {
        textWithPrefix.setValue(titleLabel.getText() + textField.getText());
    }

    public String getTextWithPrefix() {
        return textWithPrefix.get();
    }

    public StringProperty textWithPrefixProperty() {
        return textWithPrefix;
    }

    public void setTextWithPrefix(String textWithPrefix) {
        this.textWithPrefix.set(textWithPrefix);
    }

    public void hideTitle() {
        titleLabel.setText("");
    }

    public String getPromptText() {
        return textField.getPromptText();
    }

    public void setPromptText(String value) {
        textField.setPromptText(value);
    }

    public void setTitle(String title) {
        this.titleLabel.setText(title);
    }

    public String getTitle() {
        return this.titleLabel.getText();
    }

    public Label getTitleLabel() {
        return titleLabel;
    }

    public TextField getTextField() {
        return textField;
    }

    public EventHandler<ActionEvent> getOnAction() {
        return textField.getOnAction();
    }

    public void setOnAction(EventHandler<ActionEvent> onAction) {
        textField.setOnAction(onAction);
    }

    public boolean isUseAutoTitles() {
        return useAutoTitles;
    }

    public void setUseAutoTitles(boolean useAutoTitles) {
        this.useAutoTitles = useAutoTitles;
    }
}
