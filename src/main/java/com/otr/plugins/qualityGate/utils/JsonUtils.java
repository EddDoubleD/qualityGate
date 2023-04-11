package com.otr.plugins.qualityGate.utils;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import lombok.experimental.UtilityClass;

@UtilityClass
public class JsonUtils {
    private static final Gson gson = new Gson();

    /**
     * Serialize an object to Json
     *
     * @param object to serialize
     */
    public static String serialize(Object object) {
        return gson.toJson(object);
    }

    /**
     * Deserialize a json representation of an object.
     *
     * @param string a json string to deserialize
     * @param clazz  the class to which you want to convert
     */
    public static <T> T deserialize(String string, Class<T> clazz) throws JsonSyntaxException {
        return gson.fromJson(string, clazz);
    }
}
