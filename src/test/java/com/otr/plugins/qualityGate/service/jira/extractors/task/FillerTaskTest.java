package com.otr.plugins.qualityGate.service.jira.extractors.task;

import net.rcarz.jiraclient.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class FillerTaskTest {
    private static final List<Map<String, String>> CONTENT = new ArrayList<>();
    private JiraClient client;

    private Issue issue;

    @BeforeEach
    void setUp() {
        this.issue = mock(Issue.class);
        when(issue.getKey()).thenReturn("ISSUE-1");
        // mock issuetype
        IssueType type = mock(IssueType.class);
        when(type.getName()).thenReturn("issue");
        when(issue.getIssueType()).thenReturn(type);
        // mock issue state
        Status status = mock(Status.class);
        when(status.getName()).thenReturn("close");
        when(issue.getStatus()).thenReturn(status);

        this.client = mock(JiraClient.class);
    }

    @Test
    void call() throws JiraException {
        Map<String, String> issues = new HashMap<>();
        issues.put("issues",  ";ISSUE-1;ISSUE-2;");
        CONTENT.add(issues);
        Issue.SearchResult searchResult = new Issue.SearchResult();
        searchResult.issues = List.of(issue);
        when(client.searchIssues("key in (,ISSUE-2,ISSUE-1)", "issuetype,status")).thenReturn(searchResult);
        FillerTask task = new FillerTask(client, CONTENT);
        task.call();
        assertEquals("close", CONTENT.get(0).get("status"));

    }


    @Test
    void callThrow() throws JiraException {
        when(client.searchIssues("key in ()", "issuetype,status")).thenThrow(JiraException.class);
        FillerTask task = new FillerTask(client, CONTENT);
        task.call();
        assertEquals(0, CONTENT.size());
    }


}