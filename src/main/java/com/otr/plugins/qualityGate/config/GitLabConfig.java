package com.otr.plugins.qualityGate.config;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Setter
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
@ConfigurationProperties(prefix = "gitlab")
@Component
@Slf4j
public class GitLabConfig {
    @Value("gitlab.url")
    String url;

    @Value("gitlab.token")
    String token;
}
