package cz.muni.physics.utils;

import java.util.regex.Pattern;

/**
 * @author Michal Krajčovič
 * @version 1.0
 * @since 03/04/16
 */
public class Test {
    public static void main(String[] args) {
/*        *//*
        UriTemplate uriTemplate = new UriTemplate("http://www.google.com?obj={id}&ra={ra}&dec={dec}");
        Map<String, String> params = new HashMap<>();
        params.put("id", "999");
        params.put("ra", "888");
        params.put("dec", "777");
        // regex groups ?<id>
        URI uri = uriTemplate.expand(params);
        System.out.println(uri);
        *//*
        long start;
        long end;
        long totalTime = 0;

        String input = "NSVS\\s(?<id>\\d*)(?<obj>)";
        for (int i = 0; i < 10000; i++) {
            start = System.currentTimeMillis();
            Set<String> namedGroups = new TreeSet<String>();
            Matcher m = Pattern.compile("\\(\\?<([a-zA-Z][a-zA-Z0-9]*)>").matcher(input);

            while (m.find()) {
                namedGroups.add(m.group(1));
            }
            end = System.currentTimeMillis();
            totalTime += (end-start);
//            System.out.println("regex: " + (end - start) + "ms and found " + namedGroups.size());
        }
        System.out.println(totalTime);

        totalTime = 0;
        for (int k = 0; k < 10000; k++) {
            start = System.currentTimeMillis();

            Set<String> groups2 = new TreeSet<>();
            String groupName = "";
            char[] chars = input.toCharArray();
            for (int i = 0; i < chars.length - 2; i++) {
                if (chars[i] == '(' && chars[i+1] == '?' && chars[i + 2] == '<') {
                    i++;
                    for (int j = i; j < chars.length; j++) {
                        if (chars[j] == '>') {
                            groups2.add(groupName);
                            groupName = "";
                            break;
                        }
                        groupName += chars[j];
                    }
                }
            }
            end = System.currentTimeMillis();
            totalTime += (end - start);
//            System.out.println("loopity loop: " + (end - start) + "ms and found " + groups2.size());
        }
        System.out.println(totalTime);
    */
        Pattern p = Pattern.compile("");
        System.out.println(p.toString());
    }
}
