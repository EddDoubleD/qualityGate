package com.otr.plugins.qualityGate.service.handler;

import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;

class NoneHandlerTest {

    @Test
    void handle() {
        assertEquals(Collections.emptyMap(), (new NoneHandler()).handle(null));
    }
}