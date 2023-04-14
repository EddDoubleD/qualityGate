package com.otr.plugins.qualityGate.service.gitlab;

import com.otr.plugins.qualityGate.model.gitlab.Commit;
import com.otr.plugins.qualityGate.model.gitlab.response.CompareResult;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import java.net.http.HttpRequest;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CompareApiTest {

    @SneakyThrows
    @Test
    void getCompareCommits() {
        GitLabApiExecutor executor = mock(GitLabApiExecutor.class);
        HttpRequest request = mock(HttpRequest.class);

        when(executor.buildGetRequest(anyString())).thenReturn(request);
        CompareResult compareResult = new CompareResult(List.of(new Commit(
                "1",
                "issue-1",
                "some message",
                new Date())));

        when(executor.execute(request, CompareResult.class)).thenReturn(compareResult);
        CompareApi compareApi = new CompareApi(executor);
        assertTrue(compareApi.getCompareCommits(1L, "hashFrom", "hashTo").isPresent());
    }
}