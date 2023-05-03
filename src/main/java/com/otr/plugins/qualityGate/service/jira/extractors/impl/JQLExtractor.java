package com.otr.plugins.qualityGate.service.jira.extractors.impl;

import com.otr.plugins.qualityGate.config.JiraConfig;
import com.otr.plugins.qualityGate.service.jira.NonVerifyingJiraClient;
import com.otr.plugins.qualityGate.service.jira.extractors.IssueExtractor;
import com.otr.plugins.qualityGate.utils.FunUtils;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import net.rcarz.jiraclient.Issue;
import net.rcarz.jiraclient.IssueType;
import net.rcarz.jiraclient.JiraException;
import org.springframework.stereotype.Component;
import org.springframework.util.PropertyPlaceholderHelper;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
@Component("JQL")
@Slf4j
public class JQLExtractor implements IssueExtractor {
    private static final PropertyPlaceholderHelper placeholderHelper = new PropertyPlaceholderHelper("#{", "}");
        NonVerifyingJiraClient jiraClient;

    @Override
    public List<String> extract(Issue issue, final JiraConfig.Link link) {
        String jql = placeholderHelper.replacePlaceholders(link.getJql(), new IssueResolver(issue));

        try {
            Issue.SearchResult searchResult = jiraClient.searchIssues(jql);
            return searchResult.issues.stream()
                    .map(i -> {
                        IssueType issueType = i.getIssueType();
                        String type = FunUtils.canonical(issueType.getName());
                        if (link.getIssueTypes().contains(type)) {
                            return i.getKey();
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

    private record IssueResolver(Issue issue) implements PropertyPlaceholderHelper.PlaceholderResolver {
        @Override
        public String resolvePlaceholder(String placeholderName) {
            if (placeholderName.equals("key")) {
                return issue.getKey();
            }

            return issue.getField(placeholderName).toString();
        }
    }
}
