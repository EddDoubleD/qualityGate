package com.otr.plugins.qualityGate.config;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Mail client configuration
 */
@Setter
@Getter
@ConfigurationProperties(prefix = "smtp")
@Component
@Slf4j
public class MailConfig {
    /**
     * Mail service can be disabled
     * default disable
     */
    @Value("${smtp.disable : true}")
    boolean disable;

    /**
     * smtp server host
     */
    @Value("${smtp.host : 127.0.0.1}")
    String host;

    /**
     * smtp server port
     */
    @Value("${smtp.port : 25}")
    String port;

    /**
     * use smtp authenticate
     */
    @Value("${smtp.auth : false}")
    boolean smtpAuth;

    /**
     * user smtp STAR_LTS
     */
    @Value("${smtp.tls : false}")
    boolean startTls;

    /**
     * user-login authorized on the mail server
     */
    @Value("${smtp.username : null}")
    String username;

    /**
     * user-password authorized on the mail server
     */
    @Value("${smtp.password : null}")
    String password;

    /**
     * sender user on whose behalf the email will be sent
     */
    @Value("${smtp.sender : #{null}}")
    String sender;

    /**
     * recipient list
     */
    @Value("${smtp.recipients : #{null}}")
    List<String> recipients;

    /**
     * letter subject
     */
    @Value("${smtp.subject : Announcement Notification}")
    String subject;

    /**
     * letter signature
     */
    @Value("${smtp.signature : Development team}")
    String signature;

    @Value("${smtp.directoryPath : src/main/resources}")
    String directoryPath;

    @Value("${smtp.template : email.vm}")
    String template;
}
