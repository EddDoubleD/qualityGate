package com.otr.plugins.qualityGate.model.gitlab.response;

import com.otr.plugins.qualityGate.model.gitlab.Commit;

import java.io.Serializable;
import java.util.List;


public record CompareResult(List<Commit> commits) implements Serializable {
}
