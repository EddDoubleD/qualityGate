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
import org.apache.commons.lang.mutable.MutableBoolean;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.*;

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

        final MutableBoolean mark = new MutableBoolean( true);
        handlers.forEach((k, v) -> {
            if (mark.booleanValue()) {
                log.info("Start task {}", k);
                Map<Handler.ResulType, Handler.Result> result = v.handle(param);
                log.info(String.valueOf(result.get(Handler.ResulType.SUCCESS)));
                if (result.containsKey(Handler.ResulType.ERROR) && result.get(Handler.ResulType.ERROR).getContent().size() > 0) {
                    log.error(String.valueOf(result.get(Handler.ResulType.ERROR)));
                    mark.setValue(false);
                }
            } else {
                log.warn("execution {} step will be skipped", k);
            }
        });

        stopper.stop();
    }
}
