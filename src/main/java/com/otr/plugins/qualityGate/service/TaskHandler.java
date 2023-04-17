package com.otr.plugins.qualityGate.service;


import com.otr.plugins.qualityGate.exceptions.HttpClientException;
import com.otr.plugins.qualityGate.model.AppContext;
import com.otr.plugins.qualityGate.model.gitlab.Project;
import com.otr.plugins.qualityGate.model.gitlab.Tag;
import com.otr.plugins.qualityGate.model.gitlab.response.CompareResult;
import com.otr.plugins.qualityGate.service.gitlab.CompareApi;
import com.otr.plugins.qualityGate.service.gitlab.ProjectApi;
import com.otr.plugins.qualityGate.service.gitlab.TagApi;
import com.otr.plugins.qualityGate.service.jira.JiraTaskService;
import com.otr.plugins.qualityGate.utils.TaskFilter;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.net.URISyntaxException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * The handler solves the problem of getting tasks from the difference between the last tag and the branch head
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
@Component
@Slf4j
public class TaskHandler {
    private static final String ERROR = "ERROR";
    private static final String WARNING = "WARNING";
    private static final String SUCCESS = "SUCCESS";

    ProjectApi projectApi;
    TagApi tagApi;
    CompareApi compareApi;
    JiraTaskService jiraTaskService;


    /**
     * Checking the consistency of a section of code between specified intervals
     *
     * @param context command line params
     * @return result of checking, valid keys: SUCCESS, WARNING, ERROR
     */
    public Map<String, String> handle(AppContext context) {
        return handle(context.getProjectName(), context.getStartTag(), context.getEndTag(), context.getTasks());
    }

    /**
     * Checking the consistency of a section of code between specified intervals
     *
     * @param projectName gitlab project name
     * @param tagMask     tag search mask, or tag name
     * @param branch      specifies the name of the tag or branch. If a branch is specified, the HEAD state will be taken as a comparison
     * @param tasks       list of issues to check
     * @return result of checking, valid keys: SUCCESS, WARNING, ERROR
     */
    public Map<String, String> handle(String projectName, String tagMask, String branch, List<String> tasks) {
        Map<String, String> result = new HashMap<>();

        try {
            // find project id
            Optional<Project> project = projectApi.loadProjectByName(projectName);
            // if project is not found, you need to add an entry
            if (project.isEmpty()) {
                result.put(ERROR, "Project " + projectName + " not found");
                return result;
            }
            // find last tag by mask
            Optional<Tag> tag = tagApi.searchTag(project.get().id(), tagMask);
            // if tag not found by mask is fail, but can't analysis all commits in branch
            if (tag.isEmpty()) {
                result.put(ERROR, "Tag not found by mask " + tagMask);
                return result;
            }
            // Form a list of tasks that are currently in the branch
            Optional<CompareResult> compareResult = compareApi.getCompareCommits(project.get().id(), tag.get().name(), branch);

            if (compareResult.isEmpty()) {
                result.put(WARNING, "Compare " + tag.get().name() + " -> " + branch + " result is empty");
                return result;
            }

            final Set<String> currentTasks = new HashSet<>();
            compareResult.get().commits().forEach(commit -> currentTasks.addAll(TaskFilter.parseCommit(commit)));

            Set<String> checkedTasks = currentTasks.stream().filter(jiraTaskService::checkIssue).collect(Collectors.toSet());

            // actualization task, leave some feature and bugs on links
            Set<String> actualTask = jiraTaskService.transformTasks(tasks);
            result.put(SUCCESS, checkedTasks.stream().filter(actualTask::contains).collect(Collectors.joining(",")));

            result.put(WARNING, checkedTasks.stream().filter(task -> !actualTask.contains(task)).collect(Collectors.joining(",")));

        } catch (URISyntaxException | HttpClientException e) {
            log.error(e.getMessage(), e);
            result.put(ERROR, e.getMessage());
            return result;
        }


        return result;
    }

}
