package cz.muni.physics.pdr.app.javafx;

import javafx.scene.control.Hyperlink;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

/**
 * @author Michal Krajčovič
 * @version 1.0
 * @since 25/03/16
 */
public class HyperlinkCellFactory<S> implements Callback<TableColumn<S, Hyperlink>, TableCell<S, Hyperlink>> {

    @Override
    public TableCell<S, Hyperlink> call(TableColumn<S, Hyperlink> arg) {
        TableCell<S, Hyperlink> cell = new TableCell<S, Hyperlink>() {
            @Override
            protected void updateItem(Hyperlink item, boolean empty) {
                setGraphic(item);
            }
        };
        return cell;
    }
}
