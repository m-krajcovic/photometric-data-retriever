package cz.muni.physics.plugin

import javafx.beans.property.*
import javafx.scene.control.Hyperlink

/**
 * @author Michal Krajčovič
 * @version 1.0
 * @since 20/03/16
 */
class Plugin {
    constructor(name: String, URL: String, mainFile: String, command: String, path: String, enabled: Boolean) {
        this.nameProperty = SimpleStringProperty(name)
        this.URLProperty = SimpleObjectProperty<Hyperlink>(Hyperlink(URL))
        this.mainFileProperty = SimpleStringProperty(mainFile)
        this.commandProperty = SimpleStringProperty(command)
        this.pathProperty = SimpleStringProperty(path)
        this.enabledProperty = SimpleBooleanProperty(enabled)
    }

    var nameProperty: StringProperty
    var URLProperty: ObjectProperty<Hyperlink>
    var mainFileProperty: StringProperty
    var commandProperty: StringProperty
    var pathProperty: StringProperty
    var enabledProperty: BooleanProperty

    fun getName(): String {
        return nameProperty.value
    }

    fun getURL(): String {
        return URLProperty.value.text
    }

    fun getMainFile(): String {
        return mainFileProperty.value
    }

    fun getCommand(): String {
        return commandProperty.value
    }

    fun getPath(): String {
        return pathProperty.value
    }

    fun getEnabled(): Boolean {
        return enabledProperty.value
    }

    fun setEnabled(enabled: Boolean) {
        this.enabledProperty.value = enabled
    }
}
