package cz.muni.physics.pdr.storage.converter;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import cz.muni.physics.pdr.model.Plugin;

/**
 * @author Michal Krajčovič
 * @version 1.0
 * @since 08/04/16
 */
public class PluginConverter implements Converter {
    @Override
    public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
        if (source == null) return;
        Plugin plugin = (Plugin) source;
        writer.addAttribute("name", plugin.getName());
        writer.addAttribute("mainFile", plugin.getMainFile());
        writer.addAttribute("command", plugin.getCommand());
    }

    @Override
    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
        String name = reader.getAttribute("name");
        String mainFile = reader.getAttribute("mainFile");
        String command = reader.getAttribute("command");
        return new Plugin(name, mainFile, command);
    }

    @Override
    public boolean canConvert(Class type) {
        return type.equals(Plugin.class);
    }
}
