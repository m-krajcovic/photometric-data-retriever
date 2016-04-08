package cz.muni.physics.storage.converter;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.xml.DomDriver;
import cz.muni.physics.model.StarSurvey;
import cz.muni.physics.model.Plugin;
import org.apache.commons.lang3.text.StrSubstitutor;
import org.springframework.web.util.UriTemplate;

import java.net.URI;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Michal Krajčovič
 * @version 1.0
 * @since 06/04/16
 */
public class StarSurveyConverter implements Converter {
    @Override
    public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
        if (source == null) return;
        StarSurvey record = (StarSurvey) source;
        writer.addAttribute("name", record.getName());
        writer.addAttribute("url", record.getURL());
        writer.addAttribute("sesameAlias", record.getSesameAlias());
        writer.startNode("plugin");
        if (record.getPlugin() != null)
            writer.setValue(MessageFormat.format("{0},{1},{2}", record.getPlugin().getName(),
                    record.getPlugin().getMainFile(), record.getPlugin().getCommand()));
        writer.endNode();
    }

    @Override
    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
        String starSurveyName = reader.getAttribute("name");
        String starSurveyUrl = reader.getAttribute("url");
        String starSurveyAlias = reader.getAttribute("sesameAlias");
        reader.moveDown();
        String pluginParts[] = reader.getValue().split(",");
        Plugin plugin = null;
        if (pluginParts.length == 3) {
            plugin = new Plugin(pluginParts[0], pluginParts[1], pluginParts[2]);
        }
        reader.moveUp();
        return new StarSurvey(starSurveyName, starSurveyUrl, plugin, starSurveyAlias);
    }

    @Override
    public boolean canConvert(Class type) {
        return type.equals(StarSurvey.class);
    }

    public static void main(String[] args) {
        XStream xStream = new XStream(new DomDriver());
        xStream.registerConverter(new StarSurveyConverter());

        Plugin nsvsPlugin = new Plugin("NSVS", "NsvsPlugin.jar", "java -jar {0} {1}");
        Plugin crtsPlugin = new Plugin("CRTS", "CrtsPlugin.jar", "java -jar {0} {1}");
//        Plugin crtsPlugin = plugins.stream().filter(p -> p.getName().equals("CRTS")).findFirst().get();

        StarSurvey nsvs = new StarSurvey("NSVS", "http://skydot.lanl.gov/nsvs/star.php?num={id}&mask=32004", nsvsPlugin, "NSVS\\s(?<id>\\d*)");
        StarSurvey crts = new StarSurvey("CRTS", "http://nunuku.caltech.edu/cgi-bin/getcssconedb_release_img.cgi?RA={ra}&Dec={dec}&Rad=0.2&DB=photcat&OUT=csv&SHORT=short&PLOT=plot", crtsPlugin, "");

        List<StarSurvey> recordList = new ArrayList<>();
        recordList.add(nsvs);
        recordList.add(crts);

        String x = xStream.toXML(recordList);

        System.out.println(x);


        UriTemplate uriTemplate = new UriTemplate("{url} {sranda} {sranca}");
        String url = "hi";
        String sranda = "there!";
        String sranca = "how are you";
        Map<String, String> map = new HashMap<>();
        map.put("url", url);
        map.put("sranda", sranda);
        map.put("sranca", sranca);
        URI uri = uriTemplate.expand(url,sranda,sranca);
        System.out.println(uri);

        StrSubstitutor strSubstitutor = new StrSubstitutor(map);
        String str = strSubstitutor.replace("${url} ${sranda} ${sranca} ${omg}");
        System.out.println(str);

    }
}
