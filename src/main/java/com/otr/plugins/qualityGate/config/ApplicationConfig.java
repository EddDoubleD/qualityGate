package com.otr.plugins.qualityGate.config;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

@Getter
@Setter
@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE)
@ConfigurationProperties(prefix = "quality-gate")
//@EnableConfigurationProperties({GitLabConfig.class, JiraConfig.class, MailConfig.class})
@Configuration
@Slf4j
public class ApplicationConfig {
    boolean startCommandLineRunner;
}
