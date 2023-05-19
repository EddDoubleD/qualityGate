package com.otr.plugins.qualityGate.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SplitterTest {

    @Test
    void splitToList() {
        assertEquals(0, Splitter.splitToList(null).size());
        assertEquals(3, Splitter.splitToList("A;B;C;;").size());
    }

    @Test
    void splitModeType() {
        assertEquals(1, Splitter.splitModeType("mod=LIST_OF").size());
        assertEquals(1, Splitter.splitModeType("arg1", "arg2").size());
    }
}