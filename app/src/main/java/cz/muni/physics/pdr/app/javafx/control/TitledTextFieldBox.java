package cz.muni.physics.pdr.app.javafx.control;

import javafx.application.Platform;
import javafx.beans.NamedArg;
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
 * JavaFX TextField with nice label. Allows labels to be changed in runtime, width is changed nicely and doesn't change
 * given pref size of whole input.
 * @author Michal Krajčovič
 * @version 1.0
 * @since 20/04/16
 */
public class TitledTextFieldBox extends HBox {
    private Label titleLabel;
    private TextField innerTextField;
    private UnaryOperator<TextFormatter.Change> filter = c -> c;
    private String delimiter;
    private Map<String, String> autoTitles;
    private boolean useAutoTitles = false;
    private StringProperty textWithPrefix = new SimpleStringProperty("");

    public TitledTextFieldBox() {
        this(new TextField());
    }

    public TitledTextFieldBox(@NamedArg("innerTextField") TextField innerTextField) {
        super();
        this.innerTextField = innerTextField;
        initialize();
    }

    private void initialize() {
        titleLabel = new Label();
        titleLabel.getStyleClass().add("titled-label");
        titleLabel.setFocusTraversable(false);
        titleLabel.setVisible(false);
        titleLabel.setMinWidth(Region.USE_PREF_SIZE);
        titleLabel.setMaxWidth(Region.USE_PREF_SIZE);

        widthProperty().addListener((observable, oldValue, newValue) -> {
            innerTextField.setPrefWidth(newValue.doubleValue());
        });

        heightProperty().addListener((observable, oldValue, newValue) -> {
            innerTextField.setPrefHeight(newValue.doubleValue());
            titleLabel.setPrefHeight(newValue.doubleValue());
        });

        titleLabel.textProperty().addListener((ov, prevText, currText) -> {
            Platform.runLater(() -> {
                if (currText.isEmpty()) {
                    titleLabel.setPrefWidth(0);
                    this.innerTextField.setPrefWidth(getWidth());
                    titleLabel.setVisible(false);
                    innerTextField.getStyleClass().remove("titled-text-field");
                } else {
                    Text text = new Text(currText);
                    text.setFont(titleLabel.getFont());
                    double width = text.getLayoutBounds().getWidth()
                            + titleLabel.getPadding().getLeft() + titleLabel.getPadding().getRight()
                            + 2d;
                    width = Math.ceil(width);
                    titleLabel.setPrefWidth(width);
                    this.innerTextField.setPrefWidth(getWidth() - width);
                    titleLabel.setVisible(true);
                    this.innerTextField.getStyleClass().add("titled-text-field");
                }
            });
            updateTextWithPrefix();
        });

        innerTextField.textProperty().bindBidirectional(textWithPrefix, new StringConverter<String>() {
            @Override
            public String toString(String object) {
                return object;
            }

            @Override
            public String fromString(String string) {
                return titleLabel.getText() + innerTextField.getText();

            }
        });

        innerTextField.setOnKeyPressed(event -> {
            if (event.getCode().equals(KeyCode.ESCAPE)) {
                hideTitle();
            } else if (event.getCode().equals(KeyCode.BACK_SPACE)) {
                if (innerTextField.getText().isEmpty()) {
                    hideTitle();
                }
            }
        });

        getChildren().addAll(titleLabel, innerTextField);
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
        innerTextField.setTextFormatter(new TextFormatter<>(change -> {
            if (useAutoTitles && titleLabel.getText().isEmpty()) {
                if (change.getText().equals(delimiter)) {
                    for (Map.Entry<String, String> entry : autoTitles.entrySet()) {
                        if (change.getControlNewText().startsWith(entry.getKey())) {
                            TitledTextFieldBox.this.setTitle(entry.getValue());
                            innerTextField.setText("");
                            return null;
                        }
                    }
                }
            }
            return filter.apply(change);
        }));
    }

    private void updateTextWithPrefix() {
        textWithPrefix.setValue(titleLabel.getText() + innerTextField.getText());
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
        return innerTextField.getPromptText();
    }

    public void setPromptText(String value) {
        innerTextField.setPromptText(value);
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

    public TextField getInnerTextField() {
        return innerTextField;
    }

    public EventHandler<ActionEvent> getOnAction() {
        return innerTextField.getOnAction();
    }

    public void setOnAction(EventHandler<ActionEvent> onAction) {
        innerTextField.setOnAction(onAction);
    }

    public boolean isUseAutoTitles() {
        return useAutoTitles;
    }

    public void setUseAutoTitles(boolean useAutoTitles) {
        this.useAutoTitles = useAutoTitles;
    }

}
