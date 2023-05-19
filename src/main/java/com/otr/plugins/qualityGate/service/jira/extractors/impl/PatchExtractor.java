package com.otr.plugins.qualityGate.service.jira.extractors.impl;

import com.otr.plugins.qualityGate.config.JiraConfig;
import com.otr.plugins.qualityGate.model.jira.CutIssue;
import com.otr.plugins.qualityGate.service.jira.JiraClientFactory;
import com.otr.plugins.qualityGate.service.jira.extractors.IssueExtractor;
import com.otr.plugins.qualityGate.service.jira.extractors.task.SearchCutIssue;
import com.otr.plugins.qualityGate.service.jira.utils.IssueResolver;
import com.otr.plugins.qualityGate.utils.FunUtils;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import net.rcarz.jiraclient.Issue;
import net.rcarz.jiraclient.JiraClient;
import net.rcarz.jiraclient.JiraException;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;


@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
@Component("PATCH")
@Slf4j
public class PatchExtractor implements IssueExtractor {
    private static final int BATCH = 50;

    JiraClientFactory jiraClientFactory;
    ThreadPoolTaskExecutor taskExecutor;

    @Override
    public List<CutIssue> extract(Issue issue, JiraConfig.Link link) {
        final String jql = FunUtils.resolve(link.getJql(), new IssueResolver(issue));
        try {
            JiraClient jiraClient = jiraClientFactory.jiraClient();
            // lightweight request
            Issue.SearchResult searchResult = jiraClient.searchIssues(jql, "summary", 1, 0);
            log.info("Handling Patch Issue {}", issue.getKey());

            List<Future<List<CutIssue>>> futures = new ArrayList<>();
            for (int i = 0; i < searchResult.total; i += BATCH) {
                futures.add(taskExecutor.submit(
                        new SearchCutIssue(
                                i == 0 ? jiraClient : jiraClientFactory.jiraClient(),
                                jql,
                                BATCH,
                                i
                        ))
                );
            }

            List<CutIssue> issueRows = new ArrayList<>();
            futures.forEach(future -> {
                try {
                    issueRows.addAll(future.get());
                } catch (ExecutionException | InterruptedException e) {
                    log.error(e.getMessage(), e);
                }
            });

            return issueRows;
        } catch (JiraException e) {
            log.error(e.getMessage(), e);
        }

        return Collections.emptyList();
    }
}
