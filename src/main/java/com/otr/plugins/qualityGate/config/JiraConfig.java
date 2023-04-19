package com.otr.plugins.qualityGate.config;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

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

    @Value("${jira.types:}")
    Set<String> types = new HashSet<>();

    @Value("${jira.links:}")
    Set<String> links = new HashSet<>();
}
