package com.otr.plugins.qualityGate.service;

import com.otr.plugins.qualityGate.config.post.Type;
import com.otr.plugins.qualityGate.model.LaunchParam;
import com.otr.plugins.qualityGate.service.handler.Handler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.HashMap;
import java.util.Map;

import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class RunnerTest {

    Map<Type, Handler> handlers;
    LaunchParam param;
    Stopper stopper;

    private Runner runner;

    @BeforeEach
    void setUp() {
        Handler start = mock(Handler.class);
        Handler middle = mock(Handler.class);
        Handler.Result result = new Handler.Result();
        result.add(Map.of("message", "error"));
        when(middle.handle(any())).thenReturn(Map.of(Handler.ResulType.ERROR, result));
        Handler end = mock(Handler.class);

        this.handlers = Map.of(Type.HELP, start, Type.CHANGELOG, middle, Type.LIST_OF, end);
        param = mock(LaunchParam.class);
        stopper = mock(Stopper.class);

        runner = new Runner(handlers, param, stopper);
    }

    @Test
    void run() {
        runner.run();
    }
}