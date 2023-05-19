package com.otr.plugins.qualityGate.model.gitlab;

import java.io.Serializable;
import java.util.List;

/**
 * GitLab comparison result
 * @param commits list of abbreviated representation commits
 */
public record CompareResult(List<Commit> commits) implements Serializable {
}
