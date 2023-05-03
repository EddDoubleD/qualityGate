package com.otr.plugins.qualityGate.service.jira;

import com.otr.plugins.qualityGate.config.JiraConfig;
import com.otr.plugins.qualityGate.service.jira.extractors.IssueExtractor;
import com.otr.plugins.qualityGate.service.jira.extractors.IssueExtractorFactory;
import com.otr.plugins.qualityGate.utils.FunUtils;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import net.rcarz.jiraclient.Issue;
import net.rcarz.jiraclient.IssueType;
import net.rcarz.jiraclient.JiraException;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.regex.Matcher;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
@Component
@Slf4j
public class JiraTaskService {
    JiraConfig config;
    NonVerifyingJiraClient jiraClient;
    IssueExtractorFactory extractorFactory;

    /**
     * Custom analysis of tasks in depth according to given links
     *
     * @param tasks primary task list
     * @return enriched task list
     */
    public List<String> additionalEnrichment(List<String> tasks) {
        final Map<String, JiraConfig.Link> links = new HashMap<>();
        config.getLinks().forEach((k, v) -> links.put(v.getName(), v));
        List<String> result = new ArrayList<>();
        tasks.forEach(task -> {
            try {
                Issue issue = jiraClient.getIssue(task);
                IssueType type = issue.getIssueType();
                String linkKey = FunUtils.canonical(type.getName());
                JiraConfig.Link link = Optional.ofNullable(links.get(linkKey)).orElse(links.get("DEFAULT"));
                Optional<IssueExtractor> extractor = extractorFactory.findExtractor(link.getStrategy());
                extractor.ifPresent(issueExtractor -> result.addAll(issueExtractor.extract(issue, link)));

            } catch (JiraException e) {
                log.error("ticket {} will be skipped, processing error {}", task, e.getMessage());
            }
        });

        return result;
    }



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
