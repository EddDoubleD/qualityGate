package com.otr.plugins.qualityGate.model;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class AppContext {

    String projectName;

    String startTag;

    String endTag;

    String patch;

    List<String> tasks;
}
