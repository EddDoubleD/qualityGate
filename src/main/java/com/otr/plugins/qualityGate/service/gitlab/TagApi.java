package com.otr.plugins.qualityGate.service.gitlab;

import com.otr.plugins.qualityGate.exceptions.HttpClientException;
import com.otr.plugins.qualityGate.model.gitlab.Tag;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.net.URISyntaxException;
import java.net.http.HttpRequest;
import java.util.Optional;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Component
@Slf4j
public class TagApi extends GitLabApi {

    public TagApi(GitLabApiExecutor executor) {
        super(executor);
    }


    /**
     * Searches for a tag by the given mask, get: /projects/:id/repository/tags?search=:mask
     *
     * @param id   gitlab project id
     * @param mask tag mask
     * @return last tag, matching the mask wrap to {@link Optional}, or {@link Optional#empty()}
     */
    public Optional<Tag> searchTag(Long id, String mask) throws URISyntaxException, HttpClientException {
        HttpRequest request = executor.buildGetRequest(
                "projects/" + id + "/repository/tags?search=" + mask
        );

        Tag[] tag = executor.execute(request, Tag[].class);
        if (tag == null || tag.length == 0) {
            return Optional.empty();
        }

        return Optional.of(tag[0]);
    }
}
