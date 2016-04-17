package cz.muni.physics.pdr.backend.utils;

import cz.muni.physics.pdr.backend.entity.StarSurvey;
import cz.muni.physics.pdr.backend.entity.StellarObject;
import org.apache.commons.lang3.StringUtils;
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

    public static Map<String, String> resolveParametersForSurvey(StarSurvey survey, StellarObject resolverResult) {
        Map<String, String> params = resolvePatternParameters(resolverResult.toLines(), survey.getRegexPatterns());
        resolveStarResolverParameters(resolverResult, params);
        resolveValueParameters(survey.getValueParameters(), params);
        resolveUrlParameter(survey.getUrls(), params);
        if (survey.getPlugin().getMainFile() != null && !survey.getPlugin().getMainFile().trim().isEmpty())
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

    public static Map<String, String> resolveStarResolverParameters(StellarObject result, Map<String, String> params) {
        if (result.getRightAscension() != null) params.put("ra", result.getRightAscension().toString());
        if (result.getDeclination() != null) params.put("dec", result.getDeclination().toString());
        if (StringUtils.isNotBlank(result.getPeriod())) params.put("period", result.getPeriod());
        if (StringUtils.isNotBlank(result.getEpoch())) params.put("epoch", result.getEpoch());
        params.putAll(result.getIds());
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
}