package com.otr.plugins.qualityGate.service;

import com.otr.plugins.qualityGate.config.post.Type;
import com.otr.plugins.qualityGate.model.LaunchParam;
import com.otr.plugins.qualityGate.service.handler.Handler;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.mutable.MutableBoolean;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import java.util.Map;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
@Service
@Slf4j
public class Runner implements CommandLineRunner  {
    Map<Type, Handler> handlers;

    LaunchParam param;

    Stopper stopper;

    @Override
    public void run(String... args) {
        final MutableBoolean mark = new MutableBoolean( true);
        handlers.forEach((k, v) -> {
            if (mark.booleanValue()) {
                log.info("Start task {}", k);
                Map<Handler.ResulType, Handler.Result> result = v.handle(param);
                if (result.containsKey(Handler.ResulType.ERROR) && result.get(Handler.ResulType.ERROR).getContent().size() > 0) {
                    mark.setValue(false);
                }
            } else {
                log.error("execution {} step will be skipped", k);
            }
        });

        stopper.stop();
    }
}
