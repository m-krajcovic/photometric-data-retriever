package cz.muni.physics.pdr.vizier;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Michal
 * @version 1.0
 * @since 8/19/2016
 */
public class VizierHtmlParser {
    public List<Map<String, String>> getResults(Document document) {
        Elements resultTables = document.getElementsByClass("sort");
        if (resultTables != null) {
            Element resultTable = resultTables.first();
            return getTableValues(resultTable);
        }
        return Collections.emptyList();
    }

    private List<Map<String, String>> getTableValues(Element resultTable) {
        List<Map<String, String>> result = new ArrayList<>();
        String[] header = getTableHeader(resultTable);
        Elements trs = resultTable.getElementsByTag("tr");
        for (int i = 1; i < trs.size(); i++) {
            Map<String, String> row = new HashMap<>();
            Elements tds = trs.get(i).getElementsByTag("td");
            for (int j = 0; j < tds.size(); j++) {
                Element td = tds.get(j);
                if (td.text().equals("LC")) {
                    Elements as = td.getElementsByTag("a");
                    if (as != null) {
                        row.put(header[j], as.first().attr("abs:href"));
                    }
                } else {
                    row.put(header[j], td.text());
                }
            }
            result.add(row);
        }
        return result;
    }

    private String[] getTableHeader(Element resultTable) {
        Elements ths = resultTable.getElementsByTag("th");
        String[] header = new String[ths.size()];
        for (int i = 0; i < ths.size(); i++) {
            Elements as = ths.get(i).getElementsByTag("a");
            if (as != null) {
                header[i] = as.first().text();
            } else {
                header[i] = ths.get(i).text();
            }
        }
        return header;
    }
}
