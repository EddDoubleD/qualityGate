package com.otr.plugins.qualityGate.service.jira.extractors.task;

import com.otr.plugins.qualityGate.model.jira.CutIssue;
import lombok.SneakyThrows;
import net.rcarz.jiraclient.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SearchCutIssueTest {
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

    @SneakyThrows
    @Test
    void call() {
        Issue.SearchResult result = new Issue.SearchResult();
        result.issues = singletonList(issue);
        when(client.searchIssues("key = ISSUE-1", "summary,issuetype,status", 1 ,0))
                .thenReturn(result);

        SearchCutIssue task = new SearchCutIssue(client, "key = ISSUE-1", 1, 0);
        List<CutIssue> issues = task.call();
        assertNotNull(issues);
        assertEquals(1, issues.size());
        assertEquals("ISSUE-1", issues.get(0).key());
        assertEquals("issue", issues.get(0).type());
        assertEquals("close", issues.get(0).status());
    }
}