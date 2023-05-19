package com.otr.plugins.qualityGate.service.jira.extractors.task;

import lombok.extern.slf4j.Slf4j;
import net.rcarz.jiraclient.Issue;
import net.rcarz.jiraclient.JiraClient;
import net.rcarz.jiraclient.JiraException;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

import static com.otr.plugins.qualityGate.utils.FunUtils.CUT_ISSUE;

@Slf4j
public record FillerTask(JiraClient client, List<Map<String, String>> list) implements Callable<Void> {
    private static final String JQL_SEARCH_ISSUE = "key in (:list)";
    private static final String INCLUDE_FIELDS = "issuetype,status";
    @Override
    public Void call() {
        Set<String> keys = new HashSet<>();
        list.forEach(content -> {
            String issues = content.get("issues");
            keys.addAll(List.of(issues.split(";")));
        });

        String jql = JQL_SEARCH_ISSUE.replace(":list", String.join(",", keys));

        try {
            Issue.SearchResult searchResult = client.searchIssues(jql, INCLUDE_FIELDS);
            searchResult.issues.stream()
                    .map(CUT_ISSUE)
                    .forEach(issue -> {
                        for (Map<String, String> map : list) {
                            String issues = map.get("issues");
                            if (issues.contains(issue.key())) {
                                map.putAll(issue.toMap());
                            }
                        }
                    });
        } catch (JiraException e) {
            log.error(e.getMessage());
        }

        return null;
    }
}
