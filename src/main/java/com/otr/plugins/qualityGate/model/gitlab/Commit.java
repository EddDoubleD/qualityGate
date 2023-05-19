package com.otr.plugins.qualityGate.model.gitlab;

import java.util.Date;
import java.util.Map;

/**
 * Shorthand view for commit, sufficient for analysis
 * @param id commit hash
 * @param title commit header
 * @param message commit body
 * @param created_at commit creation date
 */
public record Commit(String id, String title, String message, Date created_at) {

    /**
     * Universal output format
     * @return map of field name - field
     */
    public Map<String, String> toMap() {
        return Map.of("id", id, "title", title, "message", message);
    }
}
