package com.otr.plugins.qualityGate.service.jira;

import com.otr.plugins.qualityGate.service.jira.extractors.task.FillerTask;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
@Component
@Slf4j
public class JiraSearcher {
    private static final int BUTCH_SIZE = 50;
    JiraClientFactory jiraClientFactory;
    ThreadPoolTaskExecutor taskExecutor;

    /**
     * Enriching results with data in jira
     * @param content list of commits stuff
     */
    public void saturation(List<Map<String, String>> content) {
        // fork to size/butch_size task
        List<Future<Void>> futures = new ArrayList<>();
        for (int i = 0; i < content.size(); i += BUTCH_SIZE) {
            final List<Map<String, String>> list = content.subList(i, Math.min(i + BUTCH_SIZE, content.size()));
            futures.add(taskExecutor.submit(new FillerTask(jiraClientFactory.jiraClient(), list)));
        }

        for (Future<Void> future : futures) {
            try {
                // synchronize waiting for get results
                future.get();
            } catch (InterruptedException | ExecutionException e) {
                log.error(e.getMessage(), e);
            }
        }
    }
}
