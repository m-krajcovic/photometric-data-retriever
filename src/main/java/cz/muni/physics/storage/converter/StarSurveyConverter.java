package cz.muni.physics.storage.converter;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.xml.DomDriver;
import cz.muni.physics.model.Plugin;
import cz.muni.physics.model.StarSurvey;
import org.apache.commons.lang3.text.StrSubstitutor;
import org.springframework.web.util.UriTemplate;

import java.net.URI;
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
        StarSurvey survey = (StarSurvey) source;
        writer.addAttribute("name", survey.getName());
        writer.addAttribute("url", survey.getURL());
        writer.addAttribute("sesameAlias", survey.getSesameAlias());
        writer.startNode("plugin");
        context.convertAnother(survey.getPlugin());
        writer.endNode();
    }

    @Override
    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
        StarSurvey survey = new StarSurvey();
        survey.setName(reader.getAttribute("name"));
        survey.setURL(reader.getAttribute("url"));
        survey.setSesameAlias(reader.getAttribute("sesameAlias"));
        reader.moveDown();
        Plugin plugin = (Plugin) context.convertAnother(survey, Plugin.class);
        survey.setPlugin(plugin);
        reader.moveUp();
        return survey;
    }

    @Override
    public boolean canConvert(Class type) {
        return type.equals(StarSurvey.class);
    }

    public static void main(String[] args) {
        XStream xStream = new XStream(new DomDriver());
        xStream.alias("starSurvey", StarSurvey.class);
        xStream.registerConverter(new StarSurveyConverter());
        xStream.registerConverter(new PluginConverter());

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

        List<StarSurvey> outputList = (List<StarSurvey>) xStream.fromXML(x);

        outputList.forEach(System.out::println);

        UriTemplate uriTemplate = new UriTemplate("{url} {sranda} {sranca}");
        String url = "hi";
        String sranda = "there!";
        String sranca = "how are you";
        Map<String, String> map = new HashMap<>();
        map.put("url", url);
        map.put("sranda", sranda);
        map.put("sranca", sranca);
        URI uri = uriTemplate.expand(url, sranda, sranca);
        System.out.println(uri);

        StrSubstitutor strSubstitutor = new StrSubstitutor(map);
        String str = strSubstitutor.replace("${url} ${sranda} ${sranca} ${omg}");
        System.out.println(str);

    }
}
