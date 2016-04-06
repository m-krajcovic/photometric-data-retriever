package cz.muni.physics.storage.converter;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.xml.DomDriver;
import cz.muni.physics.model.DatabaseRecord;
import cz.muni.physics.model.Plugin;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Michal Krajčovič
 * @version 1.0
 * @since 06/04/16
 */
public class DatabaseRecordConverter implements Converter {
    @Override
    public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
        if(source == null) return;
        DatabaseRecord record = (DatabaseRecord) source;
        writer.addAttribute("name", record.getName());
        writer.addAttribute("url", record.getURL());
        writer.addAttribute("sesameAlias", record.getSesameAlias());
        writer.startNode("plugin");
        writer.setValue(MessageFormat.format("{0},{1},{2}", record.getPlugin().getName(),
                record.getPlugin().getMainFile(), record.getPlugin().getCommand()));
        writer.endNode();
    }

    @Override
    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
        String dbName = reader.getAttribute("name");
        String dbUrl = reader.getAttribute("url");
        String dbAlias = reader.getAttribute("sesameAlias");
        reader.moveDown();
        String pluginParts[] = reader.getValue().split(",");
        Plugin plugin = new Plugin(pluginParts[0], pluginParts[1], pluginParts[2]);
        reader.moveUp();
        return new DatabaseRecord(dbName, dbUrl, plugin, dbAlias);
    }

    @Override
    public boolean canConvert(Class type) {
        return type.equals(DatabaseRecord.class);
    }

    public static void main(String[] args) {
        XStream xStream = new XStream(new DomDriver());
        xStream.registerConverter(new DatabaseRecordConverter());

        Plugin nsvsPlugin = new Plugin("NSVS", "NsvsPlugin.jar", "java -jar {0} {1}"); // TODO fix this, remove path from plugin. its bullshit
        Plugin crtsPlugin = new Plugin("CRTS", "CrtsPlugin.jar", "java -jar {0} {1}");
//        Plugin crtsPlugin = plugins.stream().filter(p -> p.getName().equals("CRTS")).findFirst().get();

        DatabaseRecord nsvs = new DatabaseRecord("NSVS", "http://skydot.lanl.gov/nsvs/star.php?num={id}&mask=32004", nsvsPlugin, "NSVS\\s(?<id>\\d*)");
        DatabaseRecord crts = new DatabaseRecord("CRTS", "http://nunuku.caltech.edu/cgi-bin/getcssconedb_release_img.cgi?RA={ra}&Dec={dec}&Rad=0.2&DB=photcat&OUT=csv&SHORT=short&PLOT=plot", crtsPlugin, "");

        List<DatabaseRecord> recordList = new ArrayList<>();
        recordList.add(nsvs);
        recordList.add(crts);

        String x = xStream.toXML(recordList);

        System.out.println(x);

    }
}
