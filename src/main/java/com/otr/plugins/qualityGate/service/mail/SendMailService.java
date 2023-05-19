package com.otr.plugins.qualityGate.service.mail;

import com.otr.plugins.qualityGate.config.MailConfig;
import com.otr.plugins.qualityGate.exceptions.MailException;
import com.otr.plugins.qualityGate.exceptions.ResourceLoadingException;
import com.otr.plugins.qualityGate.service.handler.Handler;
import com.otr.plugins.qualityGate.utils.ResourceLoader;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.tools.generic.SortTool;
import org.springframework.stereotype.Component;

import javax.mail.*;
import javax.mail.internet.*;
import java.io.IOException;
import java.io.StringWriter;
import java.util.*;


/**
 * Mail sending service
 */
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Component
@Slf4j
public class SendMailService {

    MailConfig mailConfig;

    SmtpAuthenticator smtpAuthenticator;


    public void sendEmails(String content) throws MailException {
        sendEmails(content, null);
    }


    /**
     * Prepares content and sends emails
     *
     * @param content jira-task processing result
     * @param file path to attachment
     *
     * @throws MailException handle MessagingException
     */
    public void sendEmails(String content, String file) throws MailException {
        if (mailConfig.isDisable()) {
            return;
        }
        // preparing properties
        Properties properties = System.getProperties();
        properties.setProperty("mail.smtp.host", mailConfig.getHost());
        properties.setProperty("mail.smtp.port", mailConfig.getPort());
        properties.setProperty("mail.smtp.auth", String.valueOf(mailConfig.isSmtpAuth()));
        properties.setProperty("mail.smtp.starttls.enable", String.valueOf(mailConfig.isStartTls()));
        // get a new Session object
        Session session = Session.getInstance(properties, smtpAuthenticator);

        MimeMessage message = new MimeMessage(session);

        try {
            message.setFrom(new InternetAddress(mailConfig.getSender()));
        } catch (MessagingException e) {
            throw new MailException("Sender address error " + mailConfig.getSender() + " " + e.getMessage(), e);
        }

        try {
            List<InternetAddress> result = new LinkedList<>();
            mailConfig.getRecipients().forEach(s -> {
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
            message.setSubject(Optional.ofNullable(mailConfig.getSubject()).orElse("Announcement Notification"));
        } catch (MessagingException e) {
            // Well, you can live with it
            log.error(e.getMessage(), e);
        }

        try {
            Multipart multipart = new MimeMultipart();
            BodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setContent(content, "text/html; charset=UTF-8");
            multipart.addBodyPart(messageBodyPart);
            if (StringUtils.isNotEmpty(file)) {
                MimeBodyPart attachmentPart = new MimeBodyPart();
                attachmentPart.attachFile(file);
                multipart.addBodyPart(attachmentPart);
            }

            message.setContent(multipart);
            Transport.send(message);
        } catch (MessagingException | NullPointerException | IOException  e) {
            throw new MailException("Message sending error " + e.getMessage(), e);
        }
    }

    /**
     * generates html from a template
     *
     * @param report analysis result
     * @return generated message from template
     */
    public String buildHtml(Template template, Map<Handler.ResulType, Handler.Result> report) throws ResourceLoadingException {
        if (mailConfig.isDisable()) {
            return null;
        }

        if (template == null) {
            template = ResourceLoader.loadTemplate(mailConfig.getDirectoryPath(), mailConfig.getTemplate());
        }

        // Select messages without errors
        Handler.Result success = report.get(Handler.ResulType.SUCCESS);
        // Select messages with errors
        Handler.Result error = Optional.ofNullable(report.get(Handler.ResulType.ERROR)).orElse(new Handler.Result());

        VelocityContext context = new VelocityContext();
        // pre-trained parameters
        context.put("sorter", new SortTool());
        context.put("developmentTeam", mailConfig.getSignature());
        context.put("success", success.getContent());
        context.put("error", error.getContent());

        StringWriter writer = new StringWriter();
        template.merge(context, writer);

        return writer.toString();
    }
}
