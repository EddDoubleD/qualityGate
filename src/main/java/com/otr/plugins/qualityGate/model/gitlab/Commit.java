package com.otr.plugins.qualityGate.model.gitlab;

import java.util.Date;

public record Commit(String id, String title, String message, Date created_at) {
}
