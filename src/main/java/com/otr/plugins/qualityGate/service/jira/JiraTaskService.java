package com.otr.plugins.qualityGate.service.jira;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import net.rcarz.jiraclient.Issue;
import net.rcarz.jiraclient.JiraClient;
import net.rcarz.jiraclient.JiraException;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
@Component
@Slf4j
public class JiraTaskService {
    private static final String DELIMITER = ";";

    Set<String> types;
    Set<String> links;

    JiraClient jiraClient;

    public Set<String> transformTasks(String tasks) {
        final Set<String> result = new HashSet<>();
        Arrays.stream(tasks.split(DELIMITER))
                .forEach(id -> {
                    try {
                        Issue issue = jiraClient.getIssue(id);
                        if (!checkIssue(issue)) {
                            return;
                        }

                        if (checkLinks(issue)) {
                            issue.getIssueLinks().forEach(link -> {
                                Issue inwards = link.getInwardIssue();
                                if (checkIssue(inwards)) {
                                    result.add(inwards.getKey());
                                }

                                Issue outward = link.getOutwardIssue();
                                if (checkIssue(outward)) {
                                    result.add(outward.getKey());
                                }
                            });
                        } else {
                            result.add(id);
                        }
                    } catch (JiraException e) {
                        log.error(e.getMessage(), e);
                    }
                });


        return result;
    }

    /**
     * Checking a task by key
     *
     * @param key issue key
     * @return the issue exists and matches the given types
     */
    public boolean checkIssue(String key) {
        try {
            return checkIssue(jiraClient.getIssue(key));
        } catch (JiraException e) {
            log.error(e.getMessage(), e);
        }

        return false;
    }

    /**
     * Checks issue for given types
     *
     * @param issue preloaded Jira issue
     * @return the issue exists and matches the given types
     */
    public boolean checkIssue(Issue issue) {
        return issue != null
                && issue.getIssueType().getName() != null
                && types.contains(issue.getIssueType().getName().toLowerCase());
    }

    /**
     * Checking the issue task for compliance with the link format
     *
     * @param issue preloaded Jira issue
     * @return the issue exists and matches the given links
     */
    public boolean checkLinks(Issue issue) {
        return issue != null
                && issue.getIssueType().getName() != null
                && links.contains(issue.getIssueType().getName().toLowerCase());
    }
}
