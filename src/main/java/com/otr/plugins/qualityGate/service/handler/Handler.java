package com.otr.plugins.qualityGate.service.handler;

import com.otr.plugins.qualityGate.model.LaunchParam;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The handler reimplements processing logic using a combination of services
 */
public interface Handler {

    /**
     * message key
     */
    String MESSAGE = "message";


    /**
     * generates a report depending on the specified parameters
     *
     * @param param typed env params
     * @return handle result
     */
    default Map<ResulType, Result> handle(LaunchParam param) {
        // empty result
        return new HashMap<>();
    }


    enum ResulType {
        SUCCESS, WARNING, ERROR
    }


    /**
     * fixed result format
     */
    @Getter
    @NoArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    class Result {
        List<Map<String, String>> content = new ArrayList<>();

        public void add(Map<String, String> map) {
            content.add(map);
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            content.forEach(c -> {
                c.forEach((k, v) -> builder.append(k).append(": ").append(v.replaceFirst(";", "")).append(" "));
                builder.append("\n");
            });

            return builder.toString();
        }
    }
}
