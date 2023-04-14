package com.otr.plugins.qualityGate.service.gitlab;

import com.otr.plugins.qualityGate.exceptions.HttpClientException;
import com.otr.plugins.qualityGate.model.Example;
import com.otr.plugins.qualityGate.model.gitlab.Commit;
import com.otr.plugins.qualityGate.model.gitlab.GitLabSettings;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GitLabApiExecutorTest {

    private static final String URL = "https://gitlab.com/api/v4/";
    private static final String TOKEN = "TOKEN";

    private static final GitLabSettings SETTINGS = new GitLabSettings(URL, TOKEN);


    @SneakyThrows
    @Test
    void buildGetRequest() {
        GitLabApiExecutor executor = new GitLabApiExecutor(SETTINGS);
        assertFalse(executor.buildGetRequest("projects/1").headers().firstValue("PRIVATE-TOKEN").isEmpty());
    }

    @SneakyThrows
    @Test
    void executeAndHandle200() {
        HttpClient mockClient = mock(HttpClient.class);
        HttpResponse response = mock(HttpResponse.class);
        when(response.statusCode()).thenReturn(200);
        when(response.body()).thenReturn("{ \"text\": \"some text\" }");
        when(mockClient.send(any(), any())).thenReturn(response);

        GitLabApiExecutor executor = new GitLabApiExecutor(SETTINGS);
        final HttpRequest request = executor.buildGetRequest("projects/1");
        try (MockedStatic<HttpClient> client = mockStatic(HttpClient.class)) {
            client.when(HttpClient::newHttpClient).thenReturn(mockClient);
            assertEquals("some text", executor.execute(request, Example.class).text());
        }
    }

    @SneakyThrows
    @Test
    void executeAndHandle404() {
        HttpClient mockClient = mock(HttpClient.class);
        HttpResponse response = mock(HttpResponse.class);
        when(response.statusCode()).thenReturn(404);
        when(response.body()).thenReturn("{ \"message\": \"error text\" }");
        when(mockClient.send(any(), any())).thenReturn(response);

        GitLabApiExecutor executor = new GitLabApiExecutor(SETTINGS);
        final HttpRequest request = executor.buildGetRequest("projects/1");
        try (MockedStatic<HttpClient> client = mockStatic(HttpClient.class)) {
            client.when(HttpClient::newHttpClient).thenReturn(mockClient);
            assertThrows(HttpClientException.class, () -> executor.execute(request, Commit.class));
        }
    }

    @SneakyThrows
    @Test
    void executeAndHandleIOTrouble() {
        HttpClient mockClient = mock(HttpClient.class);
        when(mockClient.send(any(), any())).thenThrow(IOException.class);
        GitLabApiExecutor executor = new GitLabApiExecutor(SETTINGS);
        try (MockedStatic<HttpClient> client = mockStatic(HttpClient.class)) {
            client.when(HttpClient::newHttpClient).thenReturn(mockClient);
            assertThrows(HttpClientException.class, () -> executor.execute(null, Commit.class));
        }
    }
}