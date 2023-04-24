package com.otr.plugins.qualityGate.service.handler;

import com.otr.plugins.qualityGate.exceptions.HttpClientException;
import com.otr.plugins.qualityGate.exceptions.ResourceLoadingException;
import com.otr.plugins.qualityGate.model.LaunchParam;
import com.otr.plugins.qualityGate.model.gitlab.Commit;
import com.otr.plugins.qualityGate.model.gitlab.CompareResult;
import com.otr.plugins.qualityGate.model.gitlab.Project;
import com.otr.plugins.qualityGate.service.gitlab.CompareApi;
import com.otr.plugins.qualityGate.service.gitlab.ProjectApi;
import com.otr.plugins.qualityGate.service.jira.JiraTaskService;
import com.otr.plugins.qualityGate.service.mail.SendMailService;
import lombok.SneakyThrows;
import org.assertj.core.util.Lists;
import org.assertj.core.util.Sets;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.net.URISyntaxException;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static com.otr.plugins.qualityGate.service.handler.Handler.MESSAGE;
import static java.util.Optional.empty;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

class ChangeLogHandlerTest {

    private static final Project project = new Project(1L, "PROJECT_NAME", "description");
    private static final LaunchParam param = new LaunchParam();


    @Mock
    ProjectApi projectApi;
    @Mock
    CompareApi compareApi;
    @Mock
    JiraTaskService jiraTaskService;
    @Mock
    SendMailService sendMailService;

    @InjectMocks
    ChangeLogHandler handler;

    @BeforeEach
    void setUp() {
        param.setProjectName("PROJECT_NAME");
        param.setStartTag("v1.0");
        param.setEndTag("v1.1");

        MockitoAnnotations.openMocks(this);
    }

    /**
     * Handling project load error
     */
    @Test
    void handleLoadProjectError() throws HttpClientException, URISyntaxException {
        when(projectApi.loadProjectByName(anyString())).thenThrow(HttpClientException.class);
        // no positive results
        assertNull(handler.handle(param).get(Handler.ResulType.SUCCESS));
        // negative result with a description of the problem
        assertNotNull(handler.handle(param).get(Handler.ResulType.ERROR));
        assertEquals(1, handler.handle(param).get(Handler.ResulType.ERROR).getContent().size());
        assertEquals("Error project details loading null", handler.handle(param).get(Handler.ResulType.ERROR).getContent().get(0).get(MESSAGE));
    }

    /**
     * Handling project not found
     */
    @Test
    void handleNullProjectLoad() throws HttpClientException, URISyntaxException {
        when(projectApi.loadProjectByName(anyString())).thenReturn(empty());
        // no positive results
        assertNull(handler.handle(param).get(Handler.ResulType.SUCCESS));
        // negative result with a description of the problem
        assertNotNull(handler.handle(param).get(Handler.ResulType.ERROR));
        assertEquals(1, handler.handle(param).get(Handler.ResulType.ERROR).getContent().size());
        assertEquals("Project not found", handler.handle(param).get(Handler.ResulType.ERROR).getContent().get(0).get(MESSAGE));
    }


    /**
     * GitLab comparator error
     */
    @Test
    void handleCompareError() throws HttpClientException, URISyntaxException {
        when(projectApi.loadProjectByName(anyString())).thenReturn(Optional.of(project));
        when(compareApi.getCompareCommits(1L, "v1.0", "v1.1")).thenThrow(HttpClientException.class);

        // no positive results
        assertNull(handler.handle(param).get(Handler.ResulType.SUCCESS));
        // negative result with a description of the problem
        assertNotNull(handler.handle(param).get(Handler.ResulType.ERROR));
        assertEquals(1, handler.handle(param).get(Handler.ResulType.ERROR).getContent().size());
        assertEquals("Error get compare result null", handler.handle(param).get(Handler.ResulType.ERROR).getContent().get(0).get(MESSAGE));
    }

    @SneakyThrows
    @Test
    void handleWithEmailError() throws HttpClientException, URISyntaxException {
        when(projectApi.loadProjectByName(anyString())).thenReturn(Optional.of(project));
        List<Commit> commits = Lists.newArrayList(
                new Commit("sha-1", "TICKET-1", "DESCRIPTION", new Date()),
                new Commit("sha-2", "ISSUE-1", "DESCRIPTION", new Date())
        );
        // check literal
        CompareResult compareResult = new CompareResult(commits);
        when(compareApi.getCompareCommits(1L, "v1.0", "v1.1")).thenReturn(Optional.of(compareResult));

        when(jiraTaskService.parseTicket("ISSUE-1", "DESCRIPTION")).thenReturn(new HashSet<>());
        when(sendMailService.buildHtml(any(), anyMap())).thenReturn("MESSAGE");
        assertNotNull(handler.handle(param).get(Handler.ResulType.SUCCESS));
        // check send mail error
        when(jiraTaskService.parseTicket(any())).thenReturn(Sets.set("TICKET-1"));
        when(sendMailService.buildHtml(any(), anyMap())).thenThrow(ResourceLoadingException.class);
        assertNotNull(handler.handle(param).get(Handler.ResulType.ERROR));
    }
}