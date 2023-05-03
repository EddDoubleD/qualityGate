package com.otr.plugins.qualityGate.service.jira.extractors.impl;

import com.otr.plugins.qualityGate.config.JiraConfig;
import com.otr.plugins.qualityGate.service.jira.extractors.IssueExtractor;
import com.otr.plugins.qualityGate.utils.FunUtils;
import net.rcarz.jiraclient.Issue;
import net.rcarz.jiraclient.IssueType;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


@Component("LINK")
public class LinkExtractor implements IssueExtractor {

    @Override
    public List<String> extract(Issue issue, JiraConfig.Link link) {
        return issue.getIssueLinks().stream().map(l -> {
            Issue outward = l.getOutwardIssue();
            if (outward != null) {
                IssueType issueType = outward.getIssueType();
                String type = FunUtils.canonical(issueType.getName());
                if (link.getIssueTypes().contains(type)) {
                    return outward.getKey();
                }
            }
            return null;
        }).filter(Objects::nonNull).collect(Collectors.toList());
    }
}
