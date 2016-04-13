package cz.muni.physics.pdr.utils;

import cz.muni.physics.pdr.model.StarSurvey;
import cz.muni.physics.pdr.nameresolver.NameResolverResult;
import org.springframework.web.util.UriTemplate;

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
 * @since 11/04/16
 */
public class ParameterUtils {

    public static Map<String, String> resolveParametersForSurvey(StarSurvey survey, NameResolverResult resolverResult) {
        Map<String, String> params = resolvePatternParameters(resolverResult.toLines(), survey.getRegexPatterns());
        resolveNameResolverParameters(resolverResult, params);
        resolveValueParameters(survey.getValueParameters(), params);
        resolveUrlParameter(survey.getUrls(), params);
        params.put("mainFile", survey.getPlugin().getMainFile());
        return params;
    }

    public static Map<String, String> resolvePatternParameters(String input, List<Pattern> patterns) {
        Map<String, String> params = new HashMap<>();
        for (Pattern pattern : patterns) {
            Set<String> groups = findRegexGroups(pattern);
            Matcher m = pattern.matcher(input);
            if (m.find()) {
                for (String group : groups) {
                    params.put(group, m.group(group));
                }
            }
        }
        return params;
    }

    public static Map<String, String> resolveNameResolverParameters(NameResolverResult result, Map<String, String> params) {
        params.put("ra", result.getJraddeg());
        params.put("dec", result.getJdedeg());
        return params;
    }

    public static Map<String, String> resolveValueParameters(Map<String, String> valueParams, Map<String, String> params) {
        valueParams.forEach((key, value) -> {
            if (isResolvableWithParameters(value, params)) {
                params.put(key, value);
            }
        });
        return params;
    }

    public static Map<String, String> resolveUrlParameter(List<String> urls, Map<String, String> params) {
        for (String url : urls) {
            UriTemplate uriTemplate = new UriTemplate(url);
            if (uriTemplate.getVariableNames().stream().allMatch(params::containsKey)) {
                params.put("url", uriTemplate.expand(params).toString());
                break;
            }
        }
        return params;
    }

    public static boolean isResolvableWithParameters(String string, Map<String, String> params) {
        Set<String> stringParameters = findParameterNames(string);
        return stringParameters.stream().allMatch(params::containsKey);
    }

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

    private static final String SINGLE_QUOTE = "\'";
    private static final String DOUBLE_QUOTE = "\"";
    private static final char SLASH_CHAR = '/';
    private static final char BACKSLASH_CHAR = '\\';


    public static String quoteArgument(String argument) {
        String cleanedArgument = argument.trim();

        while (cleanedArgument.startsWith(SINGLE_QUOTE) || cleanedArgument.startsWith(DOUBLE_QUOTE)) {
            cleanedArgument = cleanedArgument.substring(1);
        }
        while (cleanedArgument.endsWith(SINGLE_QUOTE) || cleanedArgument.endsWith(DOUBLE_QUOTE)) {
            cleanedArgument = cleanedArgument.substring(0, cleanedArgument.length() - 1);
        }

        final StringBuilder buf = new StringBuilder();
        if (cleanedArgument.contains(DOUBLE_QUOTE)) {
            if (cleanedArgument.contains(SINGLE_QUOTE)) {
                throw new IllegalArgumentException(
                        "Can't handle single and double quotes in same argument");
            } else {
                return buf.append(SINGLE_QUOTE).append(cleanedArgument).append(
                        SINGLE_QUOTE).toString();
            }
        } else if (cleanedArgument.contains(SINGLE_QUOTE)
                || cleanedArgument.contains(" ")) {
            return buf.append(DOUBLE_QUOTE).append(cleanedArgument).append(
                    DOUBLE_QUOTE).toString();
        } else {
            return cleanedArgument;
        }
    }
}
