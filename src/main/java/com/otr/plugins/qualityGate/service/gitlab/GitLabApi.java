package com.otr.plugins.qualityGate.service.gitlab;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
public abstract class GitLabApi {
    protected GitLabApiExecutor executor;
}

