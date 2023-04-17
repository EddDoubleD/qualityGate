package com.otr.plugins.qualityGate.service;

import lombok.Setter;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class Stopper implements ApplicationContextAware {

    @Setter
    ApplicationContext applicationContext;


    public void stop() {
        ((ConfigurableApplicationContext) applicationContext).close();
    }
}
