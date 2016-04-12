package cz.muni.physics.pdr.storage.converter;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import cz.muni.physics.pdr.model.Plugin;
import cz.muni.physics.pdr.model.StarSurvey;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * @author Michal Krajčovič
 * @version 1.0
 * @since 06/04/16
 */
public class StarSurveyConverter implements Converter {
    private final static Logger logger = LogManager.getLogger(StarSurveyConverter.class);

    public static void main(String[] args) {
        StarSurvey survey = new StarSurvey();
        logger.debug("{}", survey.getPlugin());
    }

    @Override
    public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
        if (source == null) return;
        StarSurvey survey = (StarSurvey) source;
        writer.addAttribute("name", survey.getName());
        writer.startNode("plugin");
        if (survey.getPlugin() != null) {
            writer.addAttribute("name", survey.getPlugin().getName());
        }
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
        String pluginName = reader.getAttribute("name");
        if (pluginName != null && !pluginName.isEmpty()) {
            survey.setPlugin(new Plugin(pluginName));
        }
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
