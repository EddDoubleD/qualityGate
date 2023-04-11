package com.otr.plugins.qualityGate;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
@SpringBootApplication
public class QualityGateApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(QualityGateApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
    }
}
