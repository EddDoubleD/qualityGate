package com.otr.plugins.qualityGate.utils;


import com.otr.plugins.qualityGate.exceptions.ResourceLoadingException;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.velocity.Template;
import org.apache.velocity.app.VelocityEngine;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


/**
 * Resource loading logic is contained here
 */
@UtilityClass
@Slf4j
public class ResourceLoader {
    private static final String PATH_SEPARATOR = "/";

    public static <T> T loadJsonFile(String directoryPath, String filePath, Class<T> clazz) throws ResourceLoadingException {
        String jsonText = loadTextFile(directoryPath, filePath);
        try {
            return JsonUtils.deserialize(jsonText, clazz);
        } catch (Exception e) {
            throw new ResourceLoadingException(String.format("Json %s conversion error", filePath), e);
        }
    }

    public static String loadTextFile(String directoryPath, String filePath) throws ResourceLoadingException {
        Path path = Paths.get(directoryPath + PATH_SEPARATOR + filePath);
        if (Files.isReadable(path)) {
            try {
                return Files.readString(path);
            } catch (IOException e) {
                throw new ResourceLoadingException(String.format("File %s read error " + e.getMessage(), filePath), e);
            }
        } else {
            throw new ResourceLoadingException(String.format("The file %s is not readable", path));
        }
    }

    /**
     * Loads a template for mail notification
     *
     * @param directoryPath path to the folder with source data
     * @param templatePath  path to mail notification template file
     * @return template
     * @throws ResourceLoadingException file upload error hook
     */
    public static Template loadTemplate(String directoryPath, String templatePath) throws ResourceLoadingException {
        try {
            VelocityEngine ve = new VelocityEngine();
            ve.setProperty("file.resource.loader.path", directoryPath);
            ve.init();
            return ve.getTemplate(templatePath, "UTF-8");
        } catch (Exception e) {
            throw new ResourceLoadingException(e.getMessage(), e);
        }
    }
}
