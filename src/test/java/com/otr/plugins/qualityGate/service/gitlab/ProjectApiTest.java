package com.otr.plugins.qualityGate.service.gitlab;

import com.otr.plugins.qualityGate.model.gitlab.Project;
import lombok.SneakyThrows;
import org.assertj.core.util.Arrays;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Array;
import java.net.http.HttpRequest;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ProjectApiTest {

    @SneakyThrows
    @Test
    void loadProjectByName() {
        GitLabApiExecutor executor = mock(GitLabApiExecutor.class);
        HttpRequest request = mock(HttpRequest.class);
        when(executor.buildGetRequest(anyString())).thenReturn(request);
        Project[] projects = new Project[] {new Project(1L, "name", "description")};
        when(executor.execute(request, Project[].class)).thenReturn(projects);
        ProjectApi projectApi = new ProjectApi(executor);
        assertTrue(projectApi.loadProjectByName("name").isPresent());
    }
}