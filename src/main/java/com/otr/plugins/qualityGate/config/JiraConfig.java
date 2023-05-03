package com.otr.plugins.qualityGate.config;

import com.otr.plugins.qualityGate.service.jira.extractors.IssueExtractor;
import jakarta.annotation.PostConstruct;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@Setter
@Getter
@ConfigurationProperties(prefix = "jira")
@Component
@Slf4j
public class JiraConfig {
    @Value("${jira.login}")
    String login;

    @Value("${jira.password}")
    String password;

    @Value("${jira.url}")
    String url;

    @Value("${jira.hack : false}")
    boolean hack;

    @Value("${jira.mask: \\w{1,4}-\\d{1,10}}")
    String mask;

    Map<String, Link> links = new HashMap<>();

    Pattern pattern;


    @PostConstruct
    public void setUp() {
        this.pattern = Pattern.compile(mask);
    }

    @Getter
    @Setter
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class Link {
        String name;
        String strategy;
        String jql;
        List<String> issueTypes;
    }
}
