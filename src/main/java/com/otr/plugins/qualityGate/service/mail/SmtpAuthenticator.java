package com.otr.plugins.qualityGate.service.mail;

import com.otr.plugins.qualityGate.config.MailConfig;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Component
public class SmtpAuthenticator extends Authenticator {

    MailConfig config;


    public SmtpAuthenticator(MailConfig config) {
        super();
        this.config = config;
    }

    @Override
    public PasswordAuthentication getPasswordAuthentication() {
        if ((config.getUsername() != null) && (!config.getUsername().isEmpty()) && (config.getPassword() != null) && (!config.getPassword().isEmpty())) {
            return new PasswordAuthentication(config.getUsername(), config.getPassword());
        }

        return null;
    }
}
