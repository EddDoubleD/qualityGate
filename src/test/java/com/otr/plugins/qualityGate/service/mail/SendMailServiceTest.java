package com.otr.plugins.qualityGate.service.mail;

import com.otr.plugins.qualityGate.config.MailConfig;
import com.otr.plugins.qualityGate.exceptions.MailException;
import com.otr.plugins.qualityGate.service.handler.Handler;
import com.otr.plugins.qualityGate.utils.ResourceLoader;
import lombok.SneakyThrows;
import org.apache.velocity.Template;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Transport;
import java.util.Collections;
import java.util.HashMap;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;

class SendMailServiceTest {

    private static final String DIR_PATH = "src/test/resources/settings";
    private static final String TEMPLATE_NAME = "email.vm";
    private static final String HTML_NAME = "expected.html";

    @Mock
    private MailConfig mailSettings;
    @Mock
    private SmtpAuthenticator smtpAuthenticator;

    private Template template;
    private String expectedHtml;

    @SneakyThrows
    @BeforeEach
    public void setUp() {
        mailSettings = mock(MailConfig.class);
        // simulate the work of settings
        Mockito.when(mailSettings.getHost()).thenReturn("smtp.user_mail.com");
        Mockito.when(mailSettings.getPort()).thenReturn("25");
        Mockito.when(mailSettings.isSmtpAuth()).thenReturn(true);
        Mockito.when(mailSettings.isStartTls()).thenReturn(false);
        Mockito.when(mailSettings.getSubject()).thenReturn("Hello World");

        Mockito.when(mailSettings.getDirectoryPath()).thenReturn("src/test/resources/settings");
        Mockito.when(mailSettings.getTemplate()).thenReturn("email.vm");

        smtpAuthenticator = mock(SmtpAuthenticator.class);

        template = ResourceLoader.loadTemplate(DIR_PATH, TEMPLATE_NAME);
        expectedHtml = ResourceLoader.loadTextFile(DIR_PATH, HTML_NAME);
    }


    @Test
    public void testSendEmailsWhen() throws MailException {
        Mockito.when(mailSettings.isDisable()).thenReturn(true);
        SendMailService mailService = new SendMailService(mailSettings, smtpAuthenticator);
        mailService.sendEmails(null);

        Mockito.when(mailSettings.isDisable()).thenReturn(false);
        Mockito.when(mailSettings.getSender()).thenReturn("i'm a broken email address");
        Mockito.when(mailSettings.getRecipients()).thenReturn(Collections.singletonList("example@example.com"));
        // Assert
        mailService = new SendMailService(mailSettings, smtpAuthenticator);
        try {
            mailService.sendEmails(null);
        } catch (Exception e) {
            Assertions.assertTrue(e instanceof MailException);
        }


        Mockito.when(mailSettings.getSender()).thenReturn("example@example.com");
        Mockito.when(mailSettings.getRecipients()).thenReturn(Collections.singletonList("i'm a broken email address"));
        mailService = new SendMailService(mailSettings, smtpAuthenticator);
        try {
            mailService.sendEmails(null);
        } catch (Exception e) {
            Assertions.assertTrue(e instanceof MailException);
        }
    }

    @Test
    public void testSendEmailsWhenThrowTransportException() {
        Mockito.when(mailSettings.isDisable()).thenReturn(false);

        Mockito.when(mailSettings.getSender()).thenReturn("test@domain.com");
        Mockito.when(mailSettings.getRecipients()).thenReturn(Collections.singletonList("example@example.com"));

        // Assert
        SendMailService mailService = new SendMailService(mailSettings, smtpAuthenticator);
        try (MockedStatic<Transport> transport = mockStatic(Transport.class)) {
            transport.when(() -> Transport.send(any(Message.class))).thenThrow(new MessagingException());
            try {
                mailService.sendEmails("");
            } catch (Exception e) {
                Assertions.assertTrue(e instanceof MailException);
            }
        }
    }

    @Test
    public void testSendEmailsWithNullContent() throws Exception {
        Mockito.when(mailSettings.isDisable()).thenReturn(false);

        Mockito.when(mailSettings.getSender()).thenReturn("test@domain.com");
        Mockito.when(mailSettings.getRecipients()).thenReturn(Collections.singletonList("example@example.com"));
        // Assert
        SendMailService mailService = new SendMailService(mailSettings, smtpAuthenticator);
        ArgumentCaptor<Message> argument = ArgumentCaptor.forClass(Message.class);
        try (MockedStatic<Transport> transport = mockStatic(Transport.class)) {
            mailService.sendEmails(null);
            transport.verify(() -> Transport.send(argument.capture()));
            // Now we can see what mailer passed to Transport.send()
            final Message msg = argument.getValue();
            Assertions.assertNotNull(msg);
            Assertions.assertEquals("test@domain.com", msg.getFrom()[0].toString());
        }

    }

    @Test
    public void testSendEmails() throws Exception {
        final String message = "The sun is shining and the grass is green";

        Mockito.when(mailSettings.isDisable()).thenReturn(false);

        Mockito.when(mailSettings.getSender()).thenReturn("test@domain.com");
        Mockito.when(mailSettings.getRecipients()).thenReturn(Collections.singletonList("example@example.com"));
        // Assert
        SendMailService mailService = new SendMailService(mailSettings, smtpAuthenticator);
        ArgumentCaptor<Message> argument = ArgumentCaptor.forClass(Message.class);
        try (MockedStatic<Transport> transport = mockStatic(Transport.class)) {
            mailService.sendEmails(message);
            transport.verify(() -> Transport.send(argument.capture()));
            // Now we can see what mailer passed to Transport.send()
            final Message msg = argument.getValue();
            Assertions.assertNotNull(msg);
            Assertions.assertEquals("test@domain.com", msg.getFrom()[0].toString());
        }
    }

    @SneakyThrows
    @Test
    void testBuildHtml() {
        Mockito.when(mailSettings.isDisable()).thenReturn(true);
        // fool test, with blank template
        Assertions.assertNull((new SendMailService(mailSettings, smtpAuthenticator)).buildHtml(null, Collections.emptyMap()));
        //
        Mockito.when(mailSettings.isDisable()).thenReturn(false);
        Mockito.when(mailSettings.getSignature()).thenReturn("dev_team");
        SendMailService mailService = new SendMailService(mailSettings, smtpAuthenticator);
        Handler.Result report = new Handler.Result();
        report.add(new HashMap<>() {{
            put("param", "VALUE");
        }});


        Assertions.assertEquals(expectedHtml, mailService.buildHtml(template, new HashMap<>() {{
            put(Handler.ResulType.SUCCESS, report);
        }}));
    }
}