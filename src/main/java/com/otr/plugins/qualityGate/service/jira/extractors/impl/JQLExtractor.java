package com.otr.plugins.qualityGate.service.jira.extractors.impl;

import com.otr.plugins.qualityGate.config.JiraConfig;
import com.otr.plugins.qualityGate.model.jira.CutIssue;
import com.otr.plugins.qualityGate.service.jira.NonVerifyingJiraClient;
import com.otr.plugins.qualityGate.service.jira.extractors.IssueExtractor;
import com.otr.plugins.qualityGate.service.jira.utils.IssueResolver;
import com.otr.plugins.qualityGate.utils.FunUtils;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import net.rcarz.jiraclient.Issue;
import net.rcarz.jiraclient.IssueType;
import net.rcarz.jiraclient.JiraException;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.otr.plugins.qualityGate.utils.FunUtils.CUT_ISSUE;


@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
@Component("JQL")
@Slf4j
public class JQLExtractor implements IssueExtractor {
    NonVerifyingJiraClient jiraClient;


    @Override
    public List<CutIssue> extract(Issue issue, final JiraConfig.Link link) {
        String jql = FunUtils.resolve(link.getJql(), new IssueResolver(issue));

        try {
            Issue.SearchResult searchResult = jiraClient.searchIssues(jql);
            return searchResult.issues.stream().map(i -> {
                        IssueType issueType = i.getIssueType();
                        String type = FunUtils.canonical(issueType.getName());
                        if (link.getIssueTypes().contains(type)) {
                            return CUT_ISSUE.apply(i);
                        }

                        return null;
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        } catch (JiraException e) {
            log.error(e.getMessage());
        }

        return Collections.emptyList();
    }
}
