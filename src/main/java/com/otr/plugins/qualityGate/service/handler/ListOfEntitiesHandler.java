package com.otr.plugins.qualityGate.service.handler;

import com.otr.plugins.qualityGate.config.post.Type;
import com.otr.plugins.qualityGate.config.post.TypeSafeQualifier;
import com.otr.plugins.qualityGate.exceptions.HttpClientException;
import com.otr.plugins.qualityGate.exceptions.MailException;
import com.otr.plugins.qualityGate.exceptions.ResourceLoadingException;
import com.otr.plugins.qualityGate.model.LaunchParam;
import com.otr.plugins.qualityGate.model.gitlab.CompareResult;
import com.otr.plugins.qualityGate.model.gitlab.Project;
import com.otr.plugins.qualityGate.model.jira.CutIssue;
import com.otr.plugins.qualityGate.service.excel.ExcelCreator;
import com.otr.plugins.qualityGate.service.gitlab.CompareApi;
import com.otr.plugins.qualityGate.service.gitlab.ProjectApi;
import com.otr.plugins.qualityGate.service.jira.JiraSearcher;
import com.otr.plugins.qualityGate.service.jira.JiraTaskService;
import com.otr.plugins.qualityGate.service.mail.SendMailService;
import com.otr.plugins.qualityGate.utils.MemoryScanner;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.net.URISyntaxException;
import java.util.*;

/**
 *
 */
@TypeSafeQualifier(Type.LIST_OF)
@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
@Slf4j
public class ListOfEntitiesHandler implements Handler {

    ProjectApi projectApi;
    CompareApi compareApi;
    JiraTaskService jiraTaskService;
    SendMailService sendMailService;

    JiraSearcher jiraSearcher;

    ExcelCreator creator;


    @Override
    public Map<ResulType, Result> handle(LaunchParam param) {
        Map<ResulType, Result> result = new HashMap<>();
        // error details, the message tags will contain a description of the error that occurred during the execution
        final Result errorDetails = new Result();
        final Result details = new Result();
        final Result warnDetails = new Result();

        MemoryScanner.howByteUsed("Начало загрузки связанных сущностей");
        final Set<CutIssue> additional = jiraTaskService.additionalEnrichment(param.getTasks());
        MemoryScanner.howByteUsed("Загружено сущностей " + additional.size());

        Optional<Project> project;
        try {
            project = projectApi.loadProjectByName();
        } catch (URISyntaxException | HttpClientException e) {
            log.error(e.getMessage(), e);
            project = Optional.empty();
            errorDetails.add(Collections.singletonMap(MESSAGE, "Error project details loading " + e.getMessage()));
        }


        if (project.isEmpty()) {
            if (errorDetails.getContent().size() == 0) {
                errorDetails.add(Collections.singletonMap(MESSAGE, "Project not found"));
            }

            result.put(ResulType.ERROR, errorDetails);
            return result;
        }

        // Form a list of tasks that are currently in the branch
        Optional<CompareResult> compareResult;
        try {
            compareResult = compareApi.getCompareCommits(project.get().id(), param.getStartTag(), param.getEndTag());
        } catch (URISyntaxException | HttpClientException e) {
            String message = "Error get compare result " + e.getMessage();
            log.error(message, e);
            errorDetails.add(Collections.singletonMap(MESSAGE, message));
            result.put(ResulType.ERROR, errorDetails);
            compareResult = Optional.empty();
        }

        compareResult.ifPresent(cr -> cr.commits().stream()
                .filter(commit -> !commit.title().startsWith("Merge"))
                .forEach(commit -> {
                    Map<String, String> map = new HashMap<>();
                    map.put("id", commit.id());
                    map.put("title", commit.title());
                    map.put("message", commit.message());

                    Set<String> tasks = jiraTaskService.parseTicket(commit.message(), commit.title());
                    if (tasks.isEmpty()) {
                        // missing issue number in commit
                        errorDetails.add(map);
                    } else {
                        Optional<String> opts = tasks.stream().filter(t -> additional.contains(new CutIssue(t, null, null))).findFirst();
                        // missing
                        if (opts.isEmpty()) {
                            map.put("issues", String.join(";", tasks));
                            warnDetails.add(map);
                        } else {
                            map.put("issues", opts.get());
                            details.add(map);
                        }
                    }
                }));


        jiraSearcher.saturation(warnDetails.getContent());

        compareResult.ifPresent(value -> result.put(ResulType.SUCCESS, details));
        compareResult.ifPresent(value -> result.put(ResulType.WARNING, warnDetails));
        compareResult.ifPresent(value -> result.put(ResulType.ERROR, errorDetails));

        String filename = creator.create(result);
        log.info("Report {} success created", filename);

        try {
            String message = sendMailService.buildHtml(null, result);
            sendMailService.sendEmails(message);

        } catch (ResourceLoadingException | MailException e) {
            log.error(e.getMessage(), e);
        }

        return result;
    }
}
