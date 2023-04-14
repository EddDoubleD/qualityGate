package com.otr.plugins.qualityGate.service.gitlab;

import com.otr.plugins.qualityGate.exceptions.HttpClientException;
import com.otr.plugins.qualityGate.model.Error;
import com.otr.plugins.qualityGate.model.gitlab.GitLabSettings;
import com.otr.plugins.qualityGate.utils.JsonUtils;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
@Component
@Slf4j
public class GitLabApiExecutor {

    private static final String PRIVATE_TOKEN = "PRIVATE-TOKEN";

    GitLabSettings settings;

    /**
     * Generating a request taking into account the settings
     *
     * @param url gitlab api url
     * @return ready http request
     * @throws URISyntaxException catch crookedly created url
     */
    public HttpRequest buildGetRequest(String url) throws URISyntaxException {
        return HttpRequest.newBuilder()
                .uri(new URI(settings.url() + url))
                .header(PRIVATE_TOKEN, settings.token())
                .GET()
                .build();
    }


    /**
     * Executing a prepared request, returning the response under the prepared class
     *
     * @param request request to execute
     * @param clazz   response format
     */
    public <T> T execute(HttpRequest request, Class<T> clazz) throws HttpClientException {

        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                Error error = JsonUtils.deserialize(response.body(), Error.class);
                throw new HttpClientException(error.message());
            }
            return JsonUtils.deserialize(response.body(), clazz);
        } catch (IOException | InterruptedException e) {
            log.error(e.getMessage(), e);
            throw new HttpClientException(e.getMessage());
        }
    }
}
