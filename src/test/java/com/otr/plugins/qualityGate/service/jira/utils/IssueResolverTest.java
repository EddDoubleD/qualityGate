package com.otr.plugins.qualityGate.service.jira.utils;

import net.rcarz.jiraclient.Issue;
import net.sf.json.JSONObject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class IssueResolverTest {

    @Test
    void resolvePlaceholder() {
        Issue issue = mock(Issue.class);
        JSONObject json = new JSONObject();
        json.element("key", "ISSUE-1");
        when(issue.getKey()).thenReturn("ISSUE-1");
        when(issue.getField("FIELD")).thenReturn(json);
        IssueResolver resolver = new IssueResolver(issue);

        assertEquals("ISSUE-1", resolver.resolvePlaceholder("KEY"));
        assertEquals("ISSUE-1", resolver.resolvePlaceholder("FIELD"));
        assertEquals("null", resolver.resolvePlaceholder("ANY"));

    }
}