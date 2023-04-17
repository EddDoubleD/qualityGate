package com.otr.plugins.qualityGate.service.mail;

import com.otr.plugins.qualityGate.exceptions.MailException;
import com.otr.plugins.qualityGate.model.mail.MailSettings;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.tools.generic.SortTool;
import org.springframework.stereotype.Component;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.StringWriter;
import java.util.*;
import java.util.stream.Collectors;


/**
 * Mail sending service
 */
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Component
@Slf4j
public class SendMailService {

    MailSettings mailSettings;

    SmtpAuthenticator smtpAuthenticator;

    public SendMailService(MailSettings mailSettings) {
        this.mailSettings = mailSettings;
        this.smtpAuthenticator = new SmtpAuthenticator(mailSettings.getUsername(), mailSettings.getPassword());
    }


    /**
     * Prepares content and sends emails
     *
     * @param content jira-task processing result
     * @throws MailException handle MessagingException
     */
    public void sendEmails(String content) throws MailException {
        if (!mailSettings.isUseNotification()) {
            return;
        }
        // preparing properties
        Properties properties = System.getProperties();
        properties.setProperty("mail.smtp.host", mailSettings.getSmtpHost());
        properties.setProperty("mail.smtp.port", mailSettings.getSmtpPort());
        properties.setProperty("mail.smtp.auth", String.valueOf(mailSettings.isSmtpAuth()));
        properties.setProperty("mail.smtp.starttls.enable", String.valueOf(mailSettings.isSmtpStartTls()));
        // get a new Session object
        Session session = Session.getInstance(properties, smtpAuthenticator);

        MimeMessage message = new MimeMessage(session);

        try {
            message.setFrom(new InternetAddress(mailSettings.getSender()));
        } catch (MessagingException e) {
            throw new MailException("Sender address error " + mailSettings.getSender() + " " + e.getMessage(), e);
        }

        try {
            List<InternetAddress> result = new LinkedList<>();
            mailSettings.getRecipients().forEach(s -> {
                try {
                    InternetAddress address = new InternetAddress(s);
                    result.add(address);
                } catch (AddressException e) {
                    log.error("Recipient address translation error: " + e.getMessage(), e);
                }
            });

            message.addRecipients(Message.RecipientType.TO, result.toArray(new InternetAddress[0]));
        } catch (MessagingException | NullPointerException e) {
            throw new MailException("Error adding recipients: " + e.getMessage(), e);
        }

        try {
            message.setSubject(Optional.ofNullable(mailSettings.getSubject()).orElse("Announcement Notification"));
        } catch (MessagingException e) {
            // Well, you can live with it
            log.error(e.getMessage(), e);
        }

        try {
            message.setContent(content, "text/html; charset=UTF-8");
            Transport.send(message);
        } catch (MessagingException | NullPointerException e) {
            throw new MailException("Message sending error " + e.getMessage(), e);
        }
    }

    /**
     * generates html from a template
     *
     * @param report jira-task processing result
     * @return generated letter
     */
    public String buildHtml(Template template, List<Map<String, String>> report) {
        if (!mailSettings.isUseNotification() || template == null) {
            return null;
        }
        // Select messages without errors
        List<Map<String, String>> success = report.stream().filter(m -> !m.containsKey("error")).collect(Collectors.toList());
        // Select messages with errors
        List<Map<String, String>> error = report.stream().filter(m -> m.containsKey("error")).collect(Collectors.toList());

        VelocityContext context = new VelocityContext();
        // pre-trained parameters
        context.put("sorter", new SortTool());
        context.put("developmentTeam", mailSettings.getSignature());
        context.put("issueList", success);
        context.put("issueErrorList", error);

        StringWriter writer = new StringWriter();
        template.merge(context, writer);

        return writer.toString();
    }
}
