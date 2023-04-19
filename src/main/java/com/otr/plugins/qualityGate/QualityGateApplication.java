package com.otr.plugins.qualityGate;

import com.otr.plugins.qualityGate.config.post.CustomAutowireCandidateResolver;
import com.otr.plugins.qualityGate.config.post.Type;
import com.otr.plugins.qualityGate.model.LaunchParam;
import com.otr.plugins.qualityGate.service.Stopper;
import com.otr.plugins.qualityGate.service.handler.Handler;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.*;
import java.util.stream.Collectors;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
@SpringBootApplication
@Slf4j
public class QualityGateApplication implements CommandLineRunner {

    Map<Type, Handler> handlers;

    LaunchParam param;

    Stopper stopper;

    public static void main(String[] args) {
        Optional<String> mod = Arrays.stream(args)
                .filter(arg -> arg.startsWith("--mod"))
                .findFirst();
        final List<Type> modes = new ArrayList<>();

        mod.ifPresent(s -> modes.addAll(Arrays.stream(s.replace("--mod=", "").split(";"))
                .map(m -> Type.valueOf(m.toUpperCase()))
                .toList()));

        SpringApplication application = new SpringApplication(QualityGateApplication.class);
        application.addInitializers(context -> context.addBeanFactoryPostProcessor(beanFactory -> {
            final DefaultListableBeanFactory factory = (DefaultListableBeanFactory) beanFactory;
            factory.setAutowireCandidateResolver(new CustomAutowireCandidateResolver(modes, factory));
        }));

        application.run(args);
    }

    @Override
    public void run(String... args) {

        handlers.forEach((k, v) -> {
            log.info("Start task {}", k);
            log.info("Result task {}", v.handle(param));
        });

        stopper.stop();
    }
}
