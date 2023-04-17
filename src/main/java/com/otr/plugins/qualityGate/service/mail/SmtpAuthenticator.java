package com.otr.plugins.qualityGate.service.mail;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SmtpAuthenticator extends Authenticator {

    String username;
    String password;

    public SmtpAuthenticator(String username, String password) {
        super();

        this.username = username;
        this.password = password;
    }

    @Override
    public PasswordAuthentication getPasswordAuthentication() {
        if ((username != null) && (!username.isEmpty()) && (password != null) && (!password.isEmpty())) {
            return new PasswordAuthentication(username, password);
        }

        return null;
    }
}
