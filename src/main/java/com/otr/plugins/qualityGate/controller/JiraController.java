package com.otr.plugins.qualityGate.controller;

import com.otr.plugins.qualityGate.service.TaskHandler;
import com.otr.plugins.qualityGate.service.jira.JiraTaskService;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("jira")
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class JiraController {

    JiraTaskService jiraTaskService;
    TaskHandler taskHandler;

    @GetMapping
    public ResponseEntity<Set<String>> getTypes(@RequestParam String tasks) {
        return new ResponseEntity<>(jiraTaskService.transformTasks(tasks), HttpStatus.OK);
    }

    @GetMapping("handle")
    public ResponseEntity<Map<String, String>> handle(@RequestParam String project,
                                                      @RequestParam String tagMask,
                                                      @RequestParam String branch,
                                                      @RequestParam String tasks) {

        return new ResponseEntity<>(taskHandler.handle(project, tagMask, branch, tasks), HttpStatus.OK);
    }
}
