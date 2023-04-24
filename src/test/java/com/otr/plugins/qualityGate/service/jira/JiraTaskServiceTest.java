package com.otr.plugins.qualityGate.service.jira;

import com.otr.plugins.qualityGate.config.JiraConfig;
import lombok.SneakyThrows;
import net.rcarz.jiraclient.Issue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.internal.util.collections.Sets;

import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class JiraTaskServiceTest {

    private static final JiraConfig config = new JiraConfig();

    @BeforeEach
    void setUp() {
        config.setPattern(Pattern.compile("(SUP|EXP)-\\d{1,10}"));
    }


    @SneakyThrows
    @Test
    void descriptionTest() {
        NonVerifyingJiraClient client = mock(NonVerifyingJiraClient.class);
        Issue issue = mock(Issue.class);
        when(issue.getDescription()).thenReturn("description");
        when(client.getIssue(anyString())).thenReturn(issue);
        JiraTaskService jiraTaskService = new JiraTaskService(config, client);
        assertEquals("description", jiraTaskService.getDescription("KEY"));
    }

    @Test
    void parseTicket() {
        JiraTaskService jiraTaskService = new JiraTaskService(config, null);
        assertEquals(Sets.newSet("EXP-1111", "EXP-1112"), jiraTaskService.parseTicket("EXP-1111\nEXP-1112"));
        assertEquals(Sets.newSet(), jiraTaskService.parseTicket("EX-1111\nEP-1112"));
    }

    @Test
    void checkTask() {
        JiraTaskService jiraTaskService = new JiraTaskService(config, null);
        assertTrue(jiraTaskService.checkTask("EXP-1111\nEXP-1112"));
        assertFalse(jiraTaskService.checkTask("EX-1111\nEP-1112"));
    }
}