package com.otr.plugins.qualityGate.utils.resolvers;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.util.PropertyPlaceholderHelper;

import java.util.Map;
import java.util.Optional;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class UrlResolver implements PropertyPlaceholderHelper.PlaceholderResolver {

    Map<String, String> params;

    @Override
    public String resolvePlaceholder(String placeholderName) {
        return Optional.ofNullable(params.get(placeholderName)).orElse("-");
    }
}
