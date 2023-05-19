package com.otr.plugins.qualityGate.service;

import org.junit.jupiter.api.Test;
import org.springframework.context.ConfigurableApplicationContext;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class StopperTest {

    @Test
    void stop() {
        Stopper stopper = new Stopper();
        stopper.setApplicationContext(mock(ConfigurableApplicationContext.class));
        stopper.stop();
    }
}