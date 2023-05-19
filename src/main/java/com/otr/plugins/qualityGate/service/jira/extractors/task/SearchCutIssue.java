package com.otr.plugins.qualityGate.service.jira.extractors.task;


import com.otr.plugins.qualityGate.model.jira.CutIssue;
import net.rcarz.jiraclient.JiraClient;

import java.util.List;
import java.util.concurrent.Callable;

import static com.otr.plugins.qualityGate.utils.FunUtils.CUT_ISSUE;

/**
 * Search for cut issue {@link CutIssue}, on request jql
 *
 * @param jiraClient rest data provider, ready for use
 * @param jql jira request
 * @param batch portion
 * @param position start position
 */
public record SearchCutIssue(JiraClient jiraClient, String jql, int batch, int position) implements Callable<List<CutIssue>> {
    private static final String INCLUDE_FIELDS = "summary,issuetype,status";

    @Override
    public List<CutIssue> call() throws Exception {
        return jiraClient.searchIssues(jql, INCLUDE_FIELDS, batch, position)
                .issues.stream()
                .map(CUT_ISSUE)
                .toList();
    }
}
