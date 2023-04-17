package com.otr.plugins.qualityGate.utils;

import lombok.experimental.UtilityClass;
import org.apache.commons.lang.StringUtils;
import org.springframework.core.env.Environment;
import org.springframework.lang.Nullable;
import org.springframework.util.PropertyPlaceholderHelper;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static java.util.Collections.emptyList;

@UtilityClass
public class PropertyUtils {
    private static final String PREFIX = "#{";
    private static final String SUFFIX = "}";
    private static final PropertyPlaceholderHelper placeholderHelper = new PropertyPlaceholderHelper(PREFIX, SUFFIX);

    /**
     * Converts delimited text to a list of lines
     *
     * @param s delimited text
     * @return list of lines
     */
    public static List<String> convertToList(String s) {
        return s == null ? emptyList() : Arrays.asList(s.split(";"));
    }

    /**
     * Performs system parameter substitution
     *
     * @param mask incoming mask
     * @param env  system params provider
     * @return string with substitution of system parameters
     */
    @Nullable
    public static String resolve(@Nullable String mask, Environment env) {
        if (StringUtils.isEmpty(mask)) {
            return mask;
        }

        return placeholderHelper.replacePlaceholders(mask, new EnvPlaceholderResolver(env));
    }

    private record EnvPlaceholderResolver(Environment env) implements PropertyPlaceholderHelper.PlaceholderResolver {

        @Override
        public String resolvePlaceholder(String placeholderName) {
            return Optional.ofNullable(env.getProperty(placeholderName)).orElse("-");
        }
    }
}
