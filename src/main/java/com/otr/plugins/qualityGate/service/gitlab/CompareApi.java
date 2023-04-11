package com.otr.plugins.qualityGate.service.gitlab;

import com.otr.plugins.qualityGate.exceptions.HttpClientException;
import com.otr.plugins.qualityGate.model.gitlab.response.CompareResult;
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
public class CompareApi extends GitLabApi {

    public CompareApi(GitLabApiExecutor executor) {
        super(executor);
    }

    public Optional<CompareResult> getCompareCommits(Long projectId, String from, String to) throws URISyntaxException, HttpClientException {
        HttpRequest request = executor.buildGetRequest(
                "projects/" + projectId + "/repository/compare?" +
                        "from=" + from +
                        "&to=" + to
        );

        CompareResult compareResult = executor.execute(request, CompareResult.class);
        if (compareResult == null) {
            return Optional.empty();
        }

        return Optional.of(compareResult);
    }
}
