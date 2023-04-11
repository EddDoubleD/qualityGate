package com.otr.plugins.qualityGate.utils;


import com.otr.plugins.qualityGate.exceptions.ResourceLoadingException;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

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
}
