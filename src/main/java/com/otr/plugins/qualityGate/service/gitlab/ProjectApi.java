package com.otr.plugins.qualityGate.service.gitlab;

import com.otr.plugins.qualityGate.exceptions.HttpClientException;
import com.otr.plugins.qualityGate.model.gitlab.Project;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.net.URISyntaxException;
import java.net.http.HttpRequest;
import java.util.Optional;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Component
@Slf4j
public class ProjectApi extends GitLabApi {
    private static final String URL = "projects?search=";

    public ProjectApi(GitLabApiExecutor executor) {
        super(executor);
    }

    public Optional<Project> loadProjectByName(String name) throws URISyntaxException, HttpClientException {
        HttpRequest request = executor.buildGetRequest(URL + name + "");
        Project[] projects = executor.execute(request, Project[].class);
        if (projects == null || projects.length == 0) {
            return Optional.empty();
        }

        return Optional.of(projects[0]);
    }

    public Optional<Project> loadProjectByName() throws URISyntaxException, HttpClientException {
        HttpRequest request = executor.buildGetRequest(URL + executor.getSettings().getProjectName() + "");
        Project[] projects = executor.execute(request, Project[].class);
        if (projects == null || projects.length == 0) {
            return Optional.empty();
        }

        return Optional.of(projects[0]);
    }

}
