package com.otr.plugins.qualityGate.service.mail;

import com.otr.plugins.qualityGate.config.MailConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SmtpAuthenticatorTest {

    private SmtpAuthenticator authenticator;
    private static final MailConfig CONFIG = new MailConfig();

    @BeforeEach
    void setUp() {
        CONFIG.setUsername("usr");
        CONFIG.setPassword("pwd");
        authenticator = new SmtpAuthenticator(CONFIG);
    }

    @Test
    void getUserName() {
        assertEquals("usr", authenticator.getConfig().getUsername());
        assertEquals("pwd", authenticator.getConfig().getPassword());
        assertNotNull(authenticator.getPasswordAuthentication());
    }

    @Test
    void getUserPassword() {
        SmtpAuthenticator nullPwdAuthenticator = new SmtpAuthenticator(new MailConfig());
        assertNull(nullPwdAuthenticator.getPasswordAuthentication());
    }
}