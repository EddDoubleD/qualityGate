package com.otr.plugins.qualityGate.service.jira.extractors;

import com.otr.plugins.qualityGate.config.JiraConfig;
import net.rcarz.jiraclient.Issue;

import java.util.Collections;
import java.util.List;


/**
 * Extract issue keys for different strategies
 */
public interface IssueExtractor {

    /**
     * Processing
     *
     * @param issue jira issue
     * @return list issue keys
     */
    default List<String> extract(Issue issue, JiraConfig.Link link) {
        return Collections.singletonList(issue.getKey());
    }
}

