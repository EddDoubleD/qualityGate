package com.otr.plugins.qualityGate.utils;

import com.otr.plugins.qualityGate.model.Example;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JsonUtilsTest {

    private static final String JSON = "{\"text\":\"some text\"}";


    @Test
    void serialize() {
        assertEquals(JSON, JsonUtils.serialize(new Example("some text")));
    }

    @Test
    void deserialize() {
        assertEquals("some text", JsonUtils.deserialize(JSON, Example.class).text());
    }
}