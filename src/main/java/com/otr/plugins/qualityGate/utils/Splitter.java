package com.otr.plugins.qualityGate.utils;

import com.otr.plugins.qualityGate.config.post.Type;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang.StringUtils;

import java.util.*;

@UtilityClass
public class Splitter {

    /**
     * Default splitter symbol
     */
    public static final String SEMICOLON = ";";

    public List<String> splitToList(String s) {
        if (s == null) {
            return Collections.emptyList();
        }

        return Arrays.stream(s.split(SEMICOLON)).filter(StringUtils::isNotEmpty).toList();
    }

    public List<Type> splitModeType(String... args) {
        final List<Type> modes = new ArrayList<>();

        Optional<String> mod = Arrays.stream(args).filter(arg -> arg.startsWith("--mod") || arg.startsWith("mod")).findFirst();

        if (mod.isEmpty()) {
            return Collections.singletonList(Type.HELP);
        }

        mod.ifPresent(s -> {
            s = s.replace("--mod=", "").replace("mod=", "");
            modes.addAll(splitToList(s).stream().map(m -> Type.valueOf(m.toUpperCase())).toList());
        });

        return modes;
    }
}
