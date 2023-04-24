package com.otr.plugins.qualityGate.service.handler;

import com.otr.plugins.qualityGate.config.post.Type;
import com.otr.plugins.qualityGate.config.post.TypeSafeQualifier;
import com.otr.plugins.qualityGate.exceptions.HttpClientException;
import com.otr.plugins.qualityGate.exceptions.MailException;
import com.otr.plugins.qualityGate.exceptions.ResourceLoadingException;
import com.otr.plugins.qualityGate.model.LaunchParam;
import com.otr.plugins.qualityGate.model.gitlab.CompareResult;
import com.otr.plugins.qualityGate.model.gitlab.Project;
import com.otr.plugins.qualityGate.service.gitlab.CompareApi;
import com.otr.plugins.qualityGate.service.gitlab.ProjectApi;
import com.otr.plugins.qualityGate.service.jira.JiraTaskService;
import com.otr.plugins.qualityGate.service.mail.SendMailService;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;


@Component
@TypeSafeQualifier(Type.CHANGELOG)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
@Slf4j
public class ChangeLogHandler implements Handler {

    ProjectApi projectApi;
    CompareApi compareApi;
    JiraTaskService jiraTaskService;
    SendMailService sendMailService;


    @Override
    public Map<ResulType, Result> handle(LaunchParam param) {
        Map<ResulType, Result> result = new HashMap<>();
        // error details, the message tags will contain a description of the error that occurred during the execution
        Result errorDetails = new Result();
        // find project id
        Optional<Project> project = Optional.empty();
        try {
            project = projectApi.loadProjectByName(param.getProjectName());
        } catch (URISyntaxException | HttpClientException e) {
            log.error("Error project details loading: {}", e.getMessage());
            errorDetails.add(new HashMap<>() {{
                put(MESSAGE, "Error project details loading " + e.getMessage());
            }});
        }

        if (project.isEmpty()) {
            if (errorDetails.getContent().size() == 0) {
                errorDetails.add(new HashMap<>() {{
                    put(MESSAGE, "Project not found");
                }});
            }

            result.put(ResulType.ERROR, errorDetails);
            return result;
        }
        // Form a list of tasks that are currently in the branch
        Optional<CompareResult> compareResult = Optional.empty();
        try {
            compareResult = compareApi.getCompareCommits(project.get().id(), param.getStartTag(), param.getEndTag());
        } catch (URISyntaxException | HttpClientException e) {
            String message = "Error get compare result " + e.getMessage();
            log.error(message, e);
            errorDetails.add(new HashMap<>() {{
                put(MESSAGE, message);
            }});
            result.put(ResulType.ERROR, errorDetails);
        }

        final Result details = new Result();
        compareResult.ifPresent(cr -> cr.commits().forEach(commit -> {
            Map<String, String> map = new HashMap<>();
            map.put("id", commit.id());

            Set<String> tasks = jiraTaskService.parseTicket(commit.message(), commit.title());
            if (tasks.isEmpty()) {
                errorDetails.add(map);
            } else {
                map.put("issues", tasks.stream().reduce(";", String::concat));
                details.add(map);
            }
        }));


        compareResult.ifPresent(value -> result.put(ResulType.SUCCESS, details));
        compareResult.ifPresent(value -> result.put(ResulType.ERROR, errorDetails));

        try {
            String message = sendMailService.buildHtml(null, result);
            sendMailService.sendEmails(message);

        } catch (ResourceLoadingException | MailException e) {
            result.get(ResulType.ERROR).add(new HashMap<>() {{
                put(MESSAGE, "error email notification: " + e.getMessage());
            }});
            log.error(e.getMessage(), e);
        }

        return result;
    }
}
