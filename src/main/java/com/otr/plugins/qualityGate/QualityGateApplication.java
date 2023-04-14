package com.otr.plugins.qualityGate;

import com.otr.plugins.qualityGate.config.ApplicationConfig;
import com.otr.plugins.qualityGate.service.TaskHandler;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Map;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
@SpringBootApplication
@Slf4j
public class QualityGateApplication implements CommandLineRunner {
    ApplicationConfig config;

    TaskHandler taskHandler;

    public static void main(String[] args) {
        SpringApplication.run(QualityGateApplication.class, args);
    }

    @Override
    public void run(String... args) {
        if (!config.isStartCommandLineRunner()) {
            return;
        }

        Map<String, String> result = taskHandler.handle(
                "ufos-func-05-4.0",
                "05fb.8.22.4100.616.*-hotfix", // hf-288
                "exp05fb.8.22.4000.617", // hotfix-288
                "EXP-245650;EXP-256034;EXP-256038;EXP-254517;EXP-244374;EXP-256047;EXP-245845;EXP-252766");

        log.info(result.toString());
    }
}
