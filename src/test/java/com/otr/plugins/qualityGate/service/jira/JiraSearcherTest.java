package com.otr.plugins.qualityGate.service.jira;

import com.otr.plugins.qualityGate.service.jira.extractors.task.FillerTask;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class JiraSearcherTest {

    private static final List<Map<String, String>> CONTENT = new ArrayList<>();
    @Mock
    private JiraClientFactory jiraClientFactory;
    @Mock
    private ThreadPoolTaskExecutor taskExecutor;
    @InjectMocks
    private JiraSearcher jiraSearcher;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void saturationEmptyList() {
        jiraSearcher.saturation(CONTENT);
    }

    @SneakyThrows
    @Test
    void saturation() {
        CONTENT.add(Map.of("key", "value"));
        Future<?> future = mock(Future.class);
        when(taskExecutor.submit(any(FillerTask.class))).thenReturn((Future) future);
        jiraSearcher.saturation(CONTENT);

        when(future.get()).thenThrow(InterruptedException.class);
        jiraSearcher.saturation(CONTENT);
        // clear content
        CONTENT.clear();
    }
}