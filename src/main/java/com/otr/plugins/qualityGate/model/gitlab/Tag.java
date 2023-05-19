package com.otr.plugins.qualityGate.model.gitlab;

/**
 * Lightweight GitLab tag view
 * @param name name of tag
 * @param message tag body
 */
public record Tag(String name, String message) {
}
