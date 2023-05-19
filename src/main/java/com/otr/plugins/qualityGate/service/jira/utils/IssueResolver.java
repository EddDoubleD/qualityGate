package com.otr.plugins.qualityGate.service.jira.utils;

import net.rcarz.jiraclient.Issue;
import net.sf.json.JSONObject;
import org.springframework.util.PropertyPlaceholderHelper;

/**
 *
 * @param issue
 */
public record IssueResolver(Issue issue) implements PropertyPlaceholderHelper.PlaceholderResolver {
    private static final String KEY = "key";
    @Override
    public String resolvePlaceholder(String placeholderName) {
        if (placeholderName.equalsIgnoreCase(KEY)) {
            return issue.getKey();
        }

        Object value = issue.getField(placeholderName);
        if (value instanceof JSONObject) {
            return String.valueOf(((JSONObject) value).get(KEY));
        }

        return String.valueOf(issue.getField(placeholderName));
    }
}