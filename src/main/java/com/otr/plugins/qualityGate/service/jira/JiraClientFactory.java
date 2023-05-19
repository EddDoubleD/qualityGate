package com.otr.plugins.qualityGate.service.jira;

import com.otr.plugins.qualityGate.config.JiraConfig;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import net.rcarz.jiraclient.JiraClient;
import org.springframework.stereotype.Service;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
@Service
public class JiraClientFactory {

    JiraConfig jiraConfig;

    public JiraClient jiraClient() {
        return new NonVerifyingJiraClient(jiraConfig);
    }
}
