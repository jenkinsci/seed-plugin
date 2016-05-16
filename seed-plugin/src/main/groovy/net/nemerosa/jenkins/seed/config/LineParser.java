package net.nemerosa.jenkins.seed.config;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class LineParser {

    public static List<String> parseLines(String text) {
        List<String> list = new ArrayList<>();
        if (StringUtils.isNotBlank(text)) {
            String[] array = StringUtils.split(text, "\n");
            for (String line : array) {
                line = StringUtils.trim(line);
                if (StringUtils.isNotBlank(line) && !StringUtils.startsWith(line, "#")) {
                    list.add(line);
                }
            }
        }
        return list;
    }

}
