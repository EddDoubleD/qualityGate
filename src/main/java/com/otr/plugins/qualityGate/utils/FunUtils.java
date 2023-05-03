package com.otr.plugins.qualityGate.utils;

import lombok.experimental.UtilityClass;

import java.util.Locale;
import java.util.function.UnaryOperator;

@UtilityClass
public class FunUtils {
    private static final UnaryOperator<String> CAST = s -> s.replace(" ", "").toLowerCase(Locale.ROOT);

    public String canonical(String s) {
        return CAST.apply(s);
    }
}
