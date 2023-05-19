package com.otr.plugins.qualityGate.service.jira.extractors;

import com.otr.plugins.qualityGate.config.JiraConfig;
import com.otr.plugins.qualityGate.model.jira.CutIssue;
import net.rcarz.jiraclient.Issue;

import java.util.Collections;
import java.util.List;

import static com.otr.plugins.qualityGate.utils.FunUtils.CUT_ISSUE;


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
    default List<CutIssue> extract(Issue issue, JiraConfig.Link link) {
        return Collections.singletonList(CUT_ISSUE.apply(issue));
    }

}

