package com.otr.plugins.qualityGate.utils;

import com.otr.plugins.qualityGate.model.jira.JiraSettings;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ResourceLoaderTest {

    private static final String SETTINGS_DIR = "src/test/resources/settings";
    private static final String FILE_NAME = "jira.json";

    @SneakyThrows
    @Test
    void loadJsonFile() {
        JiraSettings settings = ResourceLoader.loadJsonFile(SETTINGS_DIR, FILE_NAME, JiraSettings.class);
        assertEquals("corporate login", settings.login());
        assertEquals("corporate password", settings.password());
    }

    @SneakyThrows
    @Test
    void loadTextFile() {
        assertNotNull(ResourceLoader.loadTextFile(SETTINGS_DIR, FILE_NAME));
    }
}