package com.otr.plugins.qualityGate;

import com.otr.plugins.qualityGate.config.ApplicationConfig;
import com.otr.plugins.qualityGate.exceptions.MailException;
import com.otr.plugins.qualityGate.service.Stopper;
import com.otr.plugins.qualityGate.service.TaskHandler;
import com.otr.plugins.qualityGate.service.mail.SendMailService;
import com.otr.plugins.qualityGate.utils.MemoryScanner;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Collections;
import java.util.Map;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
@SpringBootApplication
@Slf4j
public class QualityGateApplication implements CommandLineRunner {
    ApplicationConfig config;

    TaskHandler taskHandler;

    SendMailService sendMailService;

    Stopper stopper;

    public static void main(String[] args) {
        SpringApplication.run(QualityGateApplication.class, args);
    }

    @Override
    public void run(String... args) {
        MemoryScanner.howByteUsed("Start");
        if (!config.isStartCommandLineRunner()) {
            return;
        }

        Map<String, String> result = taskHandler.handle(config.getContext());
        MemoryScanner.howByteUsed("Process");
        String html = sendMailService.buildHtml(config.getTemplate(), Collections.singletonList(result));
        try {
            sendMailService.sendEmails(html);
        } catch (MailException e) {
            log.warn(e.getMessage());
        }

        MemoryScanner.howByteUsed("Finish");
        log.info(result.toString());
        stopper.stop();
    }
}
