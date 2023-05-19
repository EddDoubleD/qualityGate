package com.otr.plugins.qualityGate.model.jira;

import net.rcarz.jiraclient.Issue;
import org.apache.struts.taglib.logic.GreaterEqualTag;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

/**
 * Truncated representation of jira issue {@link Issue}
 *
 * @param key    issue key {@link Issue#getKey()}
 * @param type   name of issue type {@link Issue#getIssueType()}
 * @param status name of issue status {@link Issue#getStatus()}
 */
public record CutIssue(String key, String type, String status) {

    public Map<String, String> toMap() {
        return Map.of("key", key, "type", type, "status", status);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CutIssue cutIssue = (CutIssue) o;
        return Objects.equals(key, cutIssue.key);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key);
    }
}
