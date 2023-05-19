package com.otr.plugins.qualityGate.model;

import com.otr.plugins.qualityGate.config.post.Type;
import com.otr.plugins.qualityGate.utils.Splitter;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Application launch options in GitLab stage mode
 */
@Setter
@Getter
@Component
public class LaunchParam {

    /**
     * GitLab project name, all actions can be performed relative to a given project
     */
    @Value("${gitlab.project}")
    String projectName;

    /**
     * Start tag for comparison
     */
    @Value("${tag.start}")
    String startTag;

    /**
     * End tag for comparison
     */
    @Value("${tag.end}")
    String endTag;

    /**
     * The list of jira issues, for analysis, there is the possibility of customization see
     * <a href="https://github.com/EddDoubleD/qualityGate#applicationyml">readme.md</a>
     */
    @Value("${jira.tasks}")
    List<String> tasks = new ArrayList<>();

    @PostConstruct
    public void resolveTasks() {
        tasks = Splitter.splitToList(tasks.size() == 0 ? null : tasks.get(0));
    }
}

