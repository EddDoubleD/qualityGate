package com.otr.plugins.qualityGate.service.jira;

import com.otr.plugins.qualityGate.config.JiraConfig;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import net.rcarz.jiraclient.Issue;
import net.rcarz.jiraclient.JiraException;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
@Component
@Slf4j
public class JiraTaskService {
    JiraConfig config;
    NonVerifyingJiraClient jiraClient;

    public String getDescription(String key) throws JiraException {
        Issue issue = jiraClient.getIssue(key);
        return issue.getDescription();
    }


    public Set<String> parseTicket(String... messages) {
        Set<String> result = new HashSet<>();
        Arrays.stream(messages)
                .map(message -> config.getPattern().matcher(message))
                .forEachOrdered(matcher -> {
                    while (matcher.find()) {
                        result.add(matcher.group());
                    }
                });

        return result;
    }


    /**
     * Checks the message for the presence of a task number by regular expression
     *
     * @param messages messages to check
     * @return is there an error number in the message ?
     */
    public boolean checkTask(String... messages) {
        return Arrays.stream(messages)
                .map(message -> config.getPattern().matcher(message))
                .anyMatch(Matcher::find);
    }
}
