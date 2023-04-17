package com.otr.plugins.qualityGate.service.mail;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SmtpAuthenticatorTest {

    private SmtpAuthenticator authenticator;

    @BeforeEach
    void setUp() {
        authenticator = new SmtpAuthenticator("usr", "pwd");
    }

    @Test
    void getUserName() {
        assertEquals("usr", authenticator.getUsername());
        assertEquals("pwd", authenticator.getPassword());
    }

    @Test
    void getUserPassword() {
        assertNotNull(authenticator.getPasswordAuthentication());
        SmtpAuthenticator nullPwdAuthenticator = new SmtpAuthenticator("", null);
        assertNull(nullPwdAuthenticator.getPasswordAuthentication());
        assertNull(nullPwdAuthenticator.getPassword());
    }
}