package com.otr.plugins.qualityGate.utils.resolvers;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.core.env.Environment;
import org.springframework.util.PropertyPlaceholderHelper;

import java.util.Optional;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class EnvironmentResolver implements PropertyPlaceholderHelper.PlaceholderResolver {
    Environment env;

    @Override
    public String resolvePlaceholder(String placeholderName) {
        return Optional.ofNullable(env.getProperty(placeholderName)).orElse("-");
    }
}
