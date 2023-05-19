package com.otr.plugins.qualityGate.model.gitlab;

/**
 * Lightweight GitLab project view
 * @param id global unique id of project in GitLab
 * @param name name
 * @param description description
 */
public record Project(Long id, String name, String description) {
}
