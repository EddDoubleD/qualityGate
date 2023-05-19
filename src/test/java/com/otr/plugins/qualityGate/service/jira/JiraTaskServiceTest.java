package com.otr.plugins.qualityGate.service.jira;

import com.otr.plugins.qualityGate.config.JiraConfig;
import com.otr.plugins.qualityGate.model.jira.CutIssue;
import com.otr.plugins.qualityGate.service.jira.extractors.IssueExtractorFactory;
import com.otr.plugins.qualityGate.service.jira.extractors.impl.DefaultExtractor;
import lombok.SneakyThrows;
import net.rcarz.jiraclient.Issue;
import net.rcarz.jiraclient.IssueType;
import net.rcarz.jiraclient.JiraException;
import net.rcarz.jiraclient.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.internal.util.collections.Sets;

import java.util.Arrays;
import java.util.Collections;
import java.util.regex.Pattern;

import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class JiraTaskServiceTest {

    private static final JiraConfig config = new JiraConfig();

    @BeforeEach
    void setUp() {
        config.setPattern(Pattern.compile("(SUP|EXP)-\\d{1,10}"));
        JiraConfig.Link defaultLink = new JiraConfig.Link();
        defaultLink.setName("DEFAULT");
        defaultLink.setStrategy("DEFAULT");
        defaultLink.setIssueTypes(singletonList("bug"));
        config.setLinks(Collections.singletonMap("DEFAULT", defaultLink));
    }


    @SneakyThrows
    @Test
    void descriptionTest() {
        NonVerifyingJiraClient client = mock(NonVerifyingJiraClient.class);
        Issue issue = mock(Issue.class);
        when(issue.getDescription()).thenReturn("description");
        when(client.getIssue(anyString(), anyString())).thenReturn(issue);
        JiraTaskService jiraTaskService = new JiraTaskService(config, client, null);
        assertEquals("description", jiraTaskService.getDescription("KEY"));
    }

    @Test
    void parseTicket() {
        JiraTaskService jiraTaskService = new JiraTaskService(config, null, null);
        assertEquals(Sets.newSet("EXP-1111", "EXP-1112"), jiraTaskService.parseTicket("EXP-1111\nEXP-1112"));
        assertEquals(Sets.newSet(), jiraTaskService.parseTicket("EX-1111\nEP-1112"));
    }

    @Test
    void checkTask() {
        JiraTaskService jiraTaskService = new JiraTaskService(config, null, null);
        assertTrue(jiraTaskService.checkTask("EXP-1111\nEXP-1112"));
        assertFalse(jiraTaskService.checkTask("EX-1111\nEP-1112"));
    }

    @SneakyThrows
    @Test
    void additionalEnrichmentTest() {
        DefaultExtractor defaultExtractor = new DefaultExtractor();
        IssueExtractorFactory factory = new IssueExtractorFactory(Collections.singletonMap("DEFAULT", defaultExtractor));
        NonVerifyingJiraClient jiraClient = mock(NonVerifyingJiraClient.class);
        when(jiraClient.getIssue("BUg #1", "issuetype")).thenThrow(JiraException.class);
        Issue issue = mock(Issue.class);
        when(issue.getKey()).thenReturn("BUG #1");
        IssueType issueType = mock(IssueType.class);
        when(issueType.getName()).thenReturn("bug");
        when(issue.getIssueType()).thenReturn(issueType);
        Status status = mock(Status.class);
        when(issue.getStatus()).thenReturn(status);

        when(jiraClient.getIssue("BUG #1", "issuetype")).thenReturn(issue);
        JiraTaskService jiraTaskService = new JiraTaskService(config, jiraClient, factory);
        assertNotNull(jiraTaskService.additionalEnrichment(Arrays.asList("BUg #1", "BUG #1")));
        assertEquals(1, jiraTaskService.additionalEnrichment(Arrays.asList("BUg #1", "BUG #1")).size());
        assertTrue(jiraTaskService.additionalEnrichment(Arrays.asList("BUg #1", "BUG #1")).contains(new CutIssue("BUG #1", "bug", null)));
    }
}