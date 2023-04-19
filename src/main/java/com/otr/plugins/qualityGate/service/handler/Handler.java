package com.otr.plugins.qualityGate.service.handler;

import com.otr.plugins.qualityGate.model.LaunchParam;

import java.util.HashMap;
import java.util.Map;

/**
 * The handler reimplements processing logic using a combination of services
 */
public interface Handler {

    /**
     * generates a report depending on the specified parameters
     *
     * @param param typed env params
     * @return handle result
     */
    default Map<String, Object> handle(LaunchParam param) {
        // empty result
        return new HashMap<>();
    }
}
