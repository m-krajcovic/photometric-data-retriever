package cz.muni.physics.pdr.app.javafx;

import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.util.Callback;

/**
 * @author Michal Krajčovič
 * @version 1.0
 * @since 25/03/16
 */
public class CheckBoxCellFactory<S, T> implements Callback<TableColumn<S, T>, TableCell<S, T>> {

    @Override
    public TableCell<S, T> call(TableColumn<S, T> arg) {
        return new CheckBoxTableCell();
    }
}
