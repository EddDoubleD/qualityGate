package com.otr.plugins.qualityGate.service.jira.extractors;

import com.otr.plugins.qualityGate.config.JiraConfig;
import com.otr.plugins.qualityGate.service.jira.NonVerifyingJiraClient;
import com.otr.plugins.qualityGate.service.jira.extractors.impl.DefaultExtractor;
import com.otr.plugins.qualityGate.service.jira.extractors.impl.JQLExtractor;
import com.otr.plugins.qualityGate.service.jira.extractors.impl.LinkExtractor;
import lombok.AccessLevel;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import net.rcarz.jiraclient.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.TestPropertySource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.util.Collections.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@FieldDefaults(level = AccessLevel.PRIVATE)
class IssueExtractorTest {


    final JiraConfig.Link configLink = new JiraConfig.Link();

    final DefaultExtractor defaultExtractor = new DefaultExtractor();
    final LinkExtractor linkExtractor = new LinkExtractor();

    Issue issue;
    NonVerifyingJiraClient jiraClient;

    JQLExtractor jqlExtractor;


    @BeforeEach
    void setUp() throws JiraException {
        configLink.setIssueTypes(Arrays.asList("bug", "dev"));
        configLink.setJql("SOME FIELD = #{custom_field_1} AND key = #{key}");

        Status status = mock(Status.class);
        IssueType normalType = mock(IssueType.class);

        issue = mock(Issue.class);
        when(issue.getKey()).thenReturn("DEV #1");
        when(issue.getField("custom_field_1")).thenReturn("some value");
        when(issue.getStatus()).thenReturn(status);
        when(issue.getIssueType()).thenReturn(normalType);

        IssueLink normalLink = mock(IssueLink.class);
        Issue normalIssue = mock(Issue.class);
        when(normalType.getName()).thenReturn("BUG");
        when(normalIssue.getIssueType()).thenReturn(normalType);
        when(normalIssue.getKey()).thenReturn("BUG #1");
        when(normalIssue.getStatus()).thenReturn(status);
        when(normalLink.getOutwardIssue()).thenReturn(normalIssue);

        IssueLink strangeLink = mock(IssueLink.class);
        Issue strangeIssue = mock(Issue.class);
        IssueType strangeType = mock(IssueType.class);
        when(strangeType.getName()).thenReturn("TASK");
        when(strangeIssue.getIssueType()).thenReturn(strangeType);
        when(strangeIssue.getKey()).thenReturn("TASK #1");
        when(strangeIssue.getStatus()).thenReturn(status);

        when(strangeLink.getOutwardIssue()).thenReturn(strangeIssue);
        List<IssueLink> links = new ArrayList<>() {{
            add(normalLink);
            add(strangeLink);
        }};
        when(issue.getIssueLinks()).thenReturn(links);

        jiraClient = mock(NonVerifyingJiraClient.class);
        Issue.SearchResult searchResult = mock(Issue.SearchResult.class);
        searchResult.issues = Arrays.asList(normalIssue, strangeIssue);
        when(jiraClient.searchIssues(anyString())).thenReturn(searchResult);

        jqlExtractor = new JQLExtractor(jiraClient);
    }


    @Test
    void defaultExtractor() {
        assertEquals("DEV #1", defaultExtractor. extract(issue, configLink).get(0).key());
    }

    @Test
    void linkExtract() {
        assertEquals("BUG #1", linkExtractor.extract(issue, configLink).get(0).key());
    }

    @Test
    void jqlExtractor() {
        assertEquals("BUG #1", jqlExtractor.extract(issue, configLink).get(0).key());
    }

    @SneakyThrows
    @Test
    void throwableJqlExtractor() {
        when(jiraClient.searchIssues(anyString())).thenThrow(JiraException.class);
        assertEquals(emptyList(), jqlExtractor.extract(issue, configLink));
    }

    @Test
    void issueExtractorFactoryTest() {
        IssueExtractorFactory extractorFactory = new IssueExtractorFactory(emptyMap());
        assertFalse(extractorFactory.findExtractor("LINK").isPresent());

        extractorFactory = new IssueExtractorFactory(singletonMap("LINK", linkExtractor));
        assertTrue(extractorFactory.findExtractor("LINK").isPresent());
    }

}