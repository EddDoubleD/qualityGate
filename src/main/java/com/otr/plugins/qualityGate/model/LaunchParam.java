package com.otr.plugins.qualityGate.model;

import com.otr.plugins.qualityGate.config.post.Type;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Setter
@Getter
@Component
public class LaunchParam {

    @Value("${gitlab.project}")
    String projectName;

    @Value("${tag.start}")
    String startTag;
    @Value("${tag.end}")
    String endTag;

    @Value("${jira.patch : #{null}")
    String patch;

    @Value("${jira.tasks : null}")
    List<String> tasks = new ArrayList<>();

    @PostConstruct
    public void resolveTasks() {
        tasks = tasks == null ? Collections.emptyList() : Arrays.asList(tasks.get(0).split(";"));
    }
}

