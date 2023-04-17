package com.otr.plugins.qualityGate.service.mail;

import com.otr.plugins.qualityGate.controller.MailException;
import com.otr.plugins.qualityGate.model.mail.MailSettings;
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
import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;

class SendMailServiceTest {

    private static final String DIR_PATH = "src/test/resources/settings";
    private static final String TEMPLATE_NAME = "email.vm";
    private static final String HTML_NAME = "expected.html";

    @Mock
    private MailSettings mailSettings;
    @Mock
    private SmtpAuthenticator smtpAuthenticator;

    private Template template;
    private String expectedHtml;

    @SneakyThrows
    @BeforeEach
    public void setUp() {
        mailSettings = mock(MailSettings.class);
        // simulate the work of settings
        Mockito.when(mailSettings.getSmtpHost()).thenReturn("smtp.user_mail.com");
        Mockito.when(mailSettings.getSmtpPort()).thenReturn("25");
        Mockito.when(mailSettings.isSmtpAuth()).thenReturn(true);
        Mockito.when(mailSettings.isSmtpStartTls()).thenReturn(false);
        Mockito.when(mailSettings.getSubject()).thenReturn("Hello World");

        smtpAuthenticator = mock(SmtpAuthenticator.class);

        template = ResourceLoader.loadTemplate(DIR_PATH, TEMPLATE_NAME);
        expectedHtml = ResourceLoader.loadTextFile(DIR_PATH, HTML_NAME);
    }


    @Test
    public void testSendEmailsWhen() throws MailException {
        Mockito.when(mailSettings.isUseNotification()).thenReturn(false);
        SendMailService mailService = new SendMailService(mailSettings, smtpAuthenticator);
        mailService.sendEmails(null);

        Mockito.when(mailSettings.isUseNotification()).thenReturn(true);
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
        Mockito.when(mailSettings.isUseNotification()).thenReturn(true);

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
        Mockito.when(mailSettings.isUseNotification()).thenReturn(true);

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

        Mockito.when(mailSettings.isUseNotification()).thenReturn(true);

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

    @Test
    void testBuildHtml() {
        Mockito.when(mailSettings.isUseNotification()).thenReturn(true);
        // fool test, with blank template
        Assertions.assertNull((new SendMailService(mailSettings, smtpAuthenticator))
                .buildHtml(null, Collections.emptyList()));
        //
        Mockito.when(mailSettings.getSignature()).thenReturn("dev_team");
        SendMailService mailService = new SendMailService(mailSettings, smtpAuthenticator);
        List<Map<String, String>> report = new ArrayList<>() {{
            add(new HashMap<>() {{
                put("param", "VALUE");
            }});
        }};


        Assertions.assertEquals(expectedHtml, mailService.buildHtml(template, report));
    }
}