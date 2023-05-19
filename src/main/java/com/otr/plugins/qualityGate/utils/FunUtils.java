package com.otr.plugins.qualityGate.utils;

import com.otr.plugins.qualityGate.model.jira.CutIssue;
import lombok.experimental.UtilityClass;
import net.rcarz.jiraclient.Issue;
import org.springframework.util.PropertyPlaceholderHelper;

import java.util.Locale;
import java.util.function.Function;
import java.util.function.UnaryOperator;

@UtilityClass
public class FunUtils {
    private static final UnaryOperator<String> CAST = s -> s.replace(" ", "").toLowerCase(Locale.ROOT);
    private static final PropertyPlaceholderHelper placeholderHelper = new PropertyPlaceholderHelper("#{", "}");

    public static final Function<Issue, CutIssue> CUT_ISSUE = issue -> new CutIssue(
            issue.getKey(),
            issue.getIssueType().getName(),
            issue.getStatus().getName()
    );

    public String canonical(String s) {
        return CAST.apply(s);
    }

    public String resolve(String value, PropertyPlaceholderHelper.PlaceholderResolver placeholderResolver) {
        return placeholderHelper.replacePlaceholders(value, placeholderResolver);
    }
}
