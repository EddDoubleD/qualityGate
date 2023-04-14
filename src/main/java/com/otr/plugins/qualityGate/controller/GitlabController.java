package com.otr.plugins.qualityGate.controller;

import com.otr.plugins.qualityGate.exceptions.HttpClientException;
import com.otr.plugins.qualityGate.model.gitlab.Project;
import com.otr.plugins.qualityGate.model.gitlab.Tag;
import com.otr.plugins.qualityGate.model.gitlab.response.CompareResult;
import com.otr.plugins.qualityGate.service.gitlab.CompareApi;
import com.otr.plugins.qualityGate.service.gitlab.ProjectApi;
import com.otr.plugins.qualityGate.service.gitlab.TagApi;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URISyntaxException;
import java.util.Optional;

@RestController
@RequestMapping("gitlab")
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class GitlabController {

    ProjectApi projectApi;
    TagApi tagApi;
    CompareApi compareApi;

    @GetMapping("/project/{name}")
    public ResponseEntity<Optional<Project>> invoke(@PathVariable String name) throws URISyntaxException, HttpClientException {
        return new ResponseEntity<>(projectApi.loadProjectByName(name), HttpStatus.OK);
    }

    @GetMapping("/tag")
    public ResponseEntity<Optional<Tag>> findTag(@RequestParam Long id, @RequestParam String mask) throws URISyntaxException, HttpClientException {
        return new ResponseEntity<>(tagApi.searchTag(id, mask), HttpStatus.OK);
    }


    @GetMapping("/compare")
    public ResponseEntity<Optional<CompareResult>> compare(@RequestParam Long id, @RequestParam String from, @RequestParam String to) throws URISyntaxException, HttpClientException {
        return new ResponseEntity<>(compareApi.getCompareCommits(id, from, to), HttpStatus.OK);
    }
}
