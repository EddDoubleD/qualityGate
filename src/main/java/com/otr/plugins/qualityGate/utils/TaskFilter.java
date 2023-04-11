package com.otr.plugins.qualityGate.utils;

import com.otr.plugins.qualityGate.model.gitlab.Commit;
import lombok.experimental.UtilityClass;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@UtilityClass
public class TaskFilter {
    private static final String REGEX = "\\b\\w{2,}-\\d+\\b";
    private static final Pattern PATTERN = Pattern.compile(REGEX);


    /**
     * Handling commit messages and headers
     * @param commit input commit
     * @return task ticket
     */
    public static Set<String> parseCommit(Commit commit) {
        Set<String> result = new HashSet<>();
        Matcher matcher = PATTERN.matcher(commit.title() + "\n" + commit.message());
        while (matcher.find()) {
            result.add(matcher.group());
        }

        return result;
    }
}
