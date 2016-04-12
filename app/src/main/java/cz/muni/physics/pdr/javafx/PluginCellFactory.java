package cz.muni.physics.pdr.javafx;

import cz.muni.physics.pdr.model.Plugin;
import cz.muni.physics.pdr.model.StarSurvey;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

/**
 * @author Michal Krajčovič
 * @version 1.0
 * @since 12/04/16
 */
public class PluginCellFactory implements Callback<TableColumn<StarSurvey, Plugin>, TableCell<StarSurvey, Plugin>> {
    @Override
    public TableCell<StarSurvey, Plugin> call(TableColumn<StarSurvey, Plugin> param) {
        return new TableCell<StarSurvey, Plugin>() {
            @Override
            protected void updateItem(Plugin item, boolean empty) {
                if (item != null) {
                    setText(item.getName());
                }
            }
        };
    }
}
