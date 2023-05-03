package com.otr.plugins.qualityGate.service.jira.extractors;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
@Component
public class IssueExtractorFactory {
    Map<String, IssueExtractor> extractors;

    public Optional<IssueExtractor> findExtractor(String strategy) {
        return Optional.ofNullable(extractors.get(strategy));
    }
}
