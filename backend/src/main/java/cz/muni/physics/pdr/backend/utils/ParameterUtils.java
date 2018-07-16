package cz.muni.physics.pdr.backend.utils;

import cz.muni.physics.pdr.backend.entity.StarSurvey;
import cz.muni.physics.pdr.backend.entity.StellarObject;
import org.springframework.web.util.UriTemplate;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility class to resolve parameters for Star Survey with StellarObject and patterns...
 * @author Michal Krajčovič
 * @version 1.0
 * @since 11/04/16
 */
public class ParameterUtils {

    /**
     * Returns full map of available parameters for given survey
     * @param survey
     * @param resolverResult
     * @param patterns
     * @return
     */
    public static Map<String, String> resolveParametersForSurvey(StarSurvey survey, StellarObject resolverResult, List<Pattern> patterns) {
        Map<String, String> params = resolvePatternParameters(resolverResult.paramList(), patterns);
        resolveStarResolverParameters(resolverResult, params);
        if (survey.getPlugin().getMainFile() != null && !survey.getPlugin().getMainFile().trim().isEmpty())
            params.put("mainFile", survey.getPlugin().getMainFile());

        return params;
    }

    public static Map<String, String> resolvePatternParameters(List<String> paramList, List<Pattern> patterns) {
        Map<String, String> params = new HashMap<>();
        for (Pattern pattern : patterns) {
            for (String input : paramList) {
                Set<String> groups = findRegexGroups(pattern);
                Matcher m = pattern.matcher(input);
                if (m.find()) {
                    for (String group : groups) {
                        params.put(group, m.group(group));
                    }
                }
            }
        }
        return params;
    }

    public static Map<String, String> resolveStarResolverParameters(StellarObject result, Map<String, String> params) {
        if (result.getRightAscension() != null && result.getRightAscension() != 0) {
            params.put("radeg", Double.toString(result.getRightAscension()));
            params.put("rah", Double.toString(result.getRightAscensionInHours()));
        }
        if (result.getRightAscension() != null && result.getDeclination() != 0) {
            params.put("decdeg", Double.toString(result.getDeclination()));
            params.put("dech", Double.toString(result.getDeclinationInHours()));
        }
        params.putAll(result.getIds());
        return params;
    }

    /**
     * Checks if all parameters in string are replaceable with strings from map
     * @param string
     * @param params
     * @return
     */
    public static boolean isResolvableWithParameters(String string, Map<String, String> params) {
        Set<String> stringParameters = findParameterNames(string);
        return stringParameters.stream().allMatch(params::containsKey);
    }

    /**
     * Finds names of regex groups from pattern
     * Group name is marked in pattern like (?<groupName>.*)
     * @param p
     * @return
     */
    public static Set<String> findRegexGroups(Pattern p) {
        Set<String> namedGroups = new HashSet<>();
        String groupName = "";
        char[] chars = p.pattern().toCharArray();
        for (int i = 0; i < chars.length - 2; i++) {
            if (chars[i] == '(' && chars[i + 1] == '?' && chars[i + 2] == '<') {
                i += 3;
                for (int j = i; j < chars.length; j++) {
                    if (chars[j] == '>') {
                        namedGroups.add(groupName);
                        groupName = "";
                        break;
                    }
                    groupName += chars[j];
                    i++;
                }
            }
        }
        return namedGroups;
    }

    /**
     * Returns names of parameters in given string ( Parameters is marked as ${parameter} )
     * @param str
     * @return
     */
    public static Set<String> findParameterNames(String str) {
        Set<String> params = new HashSet<>();
        String param = "";
        char[] chars = str.toCharArray();
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
        return params;
    }
}
