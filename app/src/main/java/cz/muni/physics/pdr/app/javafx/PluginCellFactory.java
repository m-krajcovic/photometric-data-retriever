package cz.muni.physics.pdr.app.javafx;

import cz.muni.physics.pdr.app.model.PluginModel;
import cz.muni.physics.pdr.app.model.StarSurveyModel;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

/**
 * @author Michal Krajčovič
 * @version 1.0
 * @since 12/04/16
 */
public class PluginCellFactory implements Callback<TableColumn<StarSurveyModel, PluginModel>, TableCell<StarSurveyModel, PluginModel>> {
    @Override
    public TableCell<StarSurveyModel, PluginModel> call(TableColumn<StarSurveyModel, PluginModel> param) {
        return new TableCell<StarSurveyModel, PluginModel>() {
            @Override
            protected void updateItem(PluginModel item, boolean empty) {
                if (item != null) {
                    setText(item.getName());
                }
            }
        };
    }
}
