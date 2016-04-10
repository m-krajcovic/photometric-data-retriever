package cz.muni.physics.pdr.storage.converter;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.xml.DomDriver;
import cz.muni.physics.pdr.model.Plugin;
import cz.muni.physics.pdr.model.StarSurvey;
import org.apache.commons.lang3.text.StrSubstitutor;
import org.springframework.web.util.UriTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Michal Krajčovič
 * @version 1.0
 * @since 06/04/16
 */
public class StarSurveyConverter implements Converter {
    public static void main(String[] args) {
        XStream xStream = new XStream(new DomDriver());
        xStream.alias("starSurvey", StarSurvey.class);
        xStream.alias("plugin", Plugin.class);
        xStream.registerConverter(new StarSurveyConverter());
        xStream.registerConverter(new PluginConverter());

        Plugin nsvsPlugin = new Plugin("NSVS", "NsvsPlugin.jar", "java -jar ${mainFile} ${url}");
        Plugin crtsPlugin = new Plugin("CRTS", "CrtsPlugin.jar", "java -jar ${mainFile} ${url}");
//        Plugin crtsPlugin = plugins.stream().filter(p -> p.getName().equals("CRTS")).findFirst().get();

        StarSurvey nsvs = new StarSurvey("NSVS", nsvsPlugin);
        StarSurvey crts = new StarSurvey("CRTS", crtsPlugin);

        nsvs.getRegexPatterns().addAll(Pattern.compile("NSVS\\s(?<id>\\d*)"));
        nsvs.getUrls().addAll("http://skydot.lanl.gov/nsvs/star.php?num={id}&mask=32004");
        crts.getUrls().addAll("http://nunuku.caltech.edu/cgi-bin/getcssconedb_release_img.cgi?RA={ra}&Dec={dec}&Rad=0.2&DB=photcat&OUT=csv&SHORT=short&PLOT=plot");

        List<StarSurvey> recordList = new ArrayList<>();
        recordList.add(nsvs);
        recordList.add(crts);

        String x = xStream.toXML(recordList);

        System.out.println(x);

        List<StarSurvey> outputList = (List<StarSurvey>) xStream.fromXML(x);
        outputList.forEach(ss -> ss.getRegexPatterns().forEach(System.out::println));
        outputList.forEach(ss -> ss.getValueParameters().forEach((s, s2) -> System.out.println(s + "->" + s2)));
        outputList.forEach(ss -> ss.getUrls().forEach(System.out::println));

        UriTemplate uriTemplate = new UriTemplate("{url} {sranda} {sranca}");
        String url = "hi";
        String sranda = "there!";
        String sranca = "how are you";
        Map<String, String> map = new HashMap<>();
        map.put("url", url);
        map.put("sranda", sranda);
        map.put("sranca", sranca);
        boolean uriIsResolvable = uriTemplate.getVariableNames().stream().allMatch(map::containsKey);
        System.out.println(uriIsResolvable);
        //URI uri = uriTemplate.expand(map);
        //System.out.println(uri);

        StrSubstitutor strSubstitutor = new StrSubstitutor(map);
        String strWithParams = "${url} ${sranda} ${sranca}";
        String str = strSubstitutor.replace(strWithParams);

        Set<String> params = new HashSet<>();
        String param = "";
        char[] chars = strWithParams.toCharArray();
        for (int i = 0; i < chars.length - 2; i++) {
            if (chars[i] == '$' && chars[i + 1] == '{') {
                i += 2;
                for (int j = i; j < chars.length; j++) {
                    if (chars[j] == '}') {
                        params.add(param);
                        param = "";
                        break;
                    }
                    param += chars[j];
                    i++;
                }
            }
        }

        boolean strIsResolvable = params.stream().allMatch(map::containsKey);
        System.out.println(strIsResolvable);
        System.out.println(str);


        String string = "RW Com \n" +
                "SDSS J123300.24+264258.2\n" +
                "HIP 061243\n" +
                "V* RW Com\n" +
                "V* RW Com\n" +
                "AN 33.1923\n" +
                "HIC 61243\n" +
                "HIP 61243\n" +
                "2MASS J12330028+2642582\n" +
                "NGP 27 118\n" +
                "NSVS 7622769\n" +
                "ROTSE1 J123300.30+264258.3\n" +
                "RX J123301.4+264255\n" +
                "SBC9 728\n" +
                "TYC 1991-1724-1\n" +
                "Wolf 423\n" +
                "12:33:00.24 +26:42:58.2\n" +
                "188.2510315\n" +
                "+26.7161768";

        Pattern p = Pattern.compile("NSVS\\s(?<id>\\d*)");

        Matcher m = p.matcher(string);
        System.out.println(m.find());
    }

    @Override
    public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
        if (source == null) return;
        StarSurvey survey = (StarSurvey) source;
        writer.addAttribute("name", survey.getName());
        writer.startNode("plugin");
        if (survey.getPlugin() != null)
            context.convertAnother(survey.getPlugin());
        writer.endNode();
        writer.startNode("patterns");
        List<Pattern> patterns = new ArrayList<>(survey.getRegexPatterns());
        context.convertAnother(patterns);
        writer.endNode();
        writer.startNode("valueParams");
        Map<String, String> params = new HashMap<>(survey.getValueParameters());
        context.convertAnother(params);
        writer.endNode();
        writer.startNode("urls");
        List<String> urls = new ArrayList<>(survey.getUrls());
        context.convertAnother(urls);
        writer.endNode();
    }

    @Override
    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
        StarSurvey survey = new StarSurvey();
        survey.setName(reader.getAttribute("name"));
        reader.moveDown();
        Plugin plugin = (Plugin) context.convertAnother(survey, Plugin.class);
        survey.setPlugin(plugin);
        reader.moveUp();
        reader.moveDown();
        List<Pattern> patterns = (ArrayList<Pattern>) context.convertAnother(survey, ArrayList.class);
        survey.getRegexPatterns().addAll(patterns);
        reader.moveUp();
        reader.moveDown();
        Map<String, String> params = (Map<String, String>) context.convertAnother(survey, HashMap.class);
        survey.getValueParameters().putAll(params);
        reader.moveUp();
        reader.moveDown();
        List<String> urls = (ArrayList<String>) context.convertAnother(survey, ArrayList.class);
        survey.getUrls().addAll(urls);
        reader.moveUp();
        return survey;
    }

    @Override
    public boolean canConvert(Class type) {
        return type.equals(StarSurvey.class);
    }
}
