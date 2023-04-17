package com.otr.plugins.qualityGate.utils;

import com.otr.plugins.qualityGate.exceptions.ResourceLoadingException;
import com.otr.plugins.qualityGate.model.jira.JiraSettings;
import lombok.SneakyThrows;
import org.apache.velocity.Template;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ResourceLoaderTest {

    private static final String SETTINGS_DIR = "src/test/resources/settings";
    private static final String FILE_NAME = "jira.json";

    private static final String TEMPLATE_F_NAME = "email.vm";
    private static final String NONEXISTEN_F_NAME = "nonexistent.txt";


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

    @Test
    void loadTemplate() throws ResourceLoadingException {
        Template template = ResourceLoader.loadTemplate(SETTINGS_DIR, TEMPLATE_F_NAME);
        assertNotNull(template);
        assertEquals(TEMPLATE_F_NAME, template.getName());

        assertThrows(ResourceLoadingException.class, () -> ResourceLoader.loadTemplate(SETTINGS_DIR, NONEXISTEN_F_NAME));
    }
}