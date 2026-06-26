package com.omniflow.lint;

import com.android.tools.lint.detector.api.Category;
import com.android.tools.lint.detector.api.Detector;
import com.android.tools.lint.detector.api.Implementation;
import com.android.tools.lint.detector.api.Issue;
import com.android.tools.lint.detector.api.JavaContext;
import com.android.tools.lint.detector.api.Location;
import com.android.tools.lint.detector.api.Scope;
import com.android.tools.lint.detector.api.Severity;
import com.android.tools.lint.client.api.UElementHandler;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jetbrains.uast.UElement;
import org.jetbrains.uast.UFile;

public final class DesignTokenDetector extends Detector implements Detector.UastScanner {
    static final Issue ISSUE = Issue.create(
            "OmniDesignTokenUsage",
            "Use OmniFlow design tokens",
            "UI and shared component files must not define raw visual constants. Use MaterialTheme, OmniTokens.spacing, OmniTokens.dimens, or OmniTokens.colors instead.",
            Category.CORRECTNESS,
            6,
            Severity.ERROR,
            new Implementation(DesignTokenDetector.class, Scope.JAVA_FILE_SCOPE)
    );

    private static final List<ForbiddenPattern> FORBIDDEN_PATTERNS = Arrays.asList(
            new ForbiddenPattern(Pattern.compile("Color\\(0x[0-9A-Fa-f_]+\\)"), "Replace hardcoded Color(0x...) with MaterialTheme.colorScheme or OmniTokens.colors."),
            new ForbiddenPattern(Pattern.compile("\\b\\d+(?:\\.\\d+)?\\.dp\\b"), "Replace inline dp values with OmniTokens.spacing or OmniTokens.dimens."),
            new ForbiddenPattern(Pattern.compile("\\b\\d+(?:\\.\\d+)?\\.sp\\b"), "Replace inline sp values with MaterialTheme.typography."),
            new ForbiddenPattern(Pattern.compile("\\bfontSize\\s*="), "Do not set fontSize inline; use MaterialTheme.typography.")
    );

    @Override
    public List<Class<? extends UElement>> getApplicableUastTypes() {
        return Collections.singletonList(UFile.class);
    }

    @Override
    public UElementHandler createUastHandler(JavaContext context) {
        return new UElementHandler() {
            @Override
            public void visitFile(UFile node) {
                String path = context.file.getPath().replace('\\', '/');
                if (!shouldCheck(path)) {
                    return;
                }

                CharSequence contents = context.getContents();
                if (contents == null) {
                    return;
                }

                for (ForbiddenPattern forbiddenPattern : FORBIDDEN_PATTERNS) {
                    Matcher matcher = forbiddenPattern.pattern.matcher(contents);
                    while (matcher.find()) {
                        Location location = Location.create(context.file, contents, matcher.start(), matcher.end());
                        context.report(ISSUE, node, location, forbiddenPattern.message);
                    }
                }
            }
        };
    }

    private static boolean shouldCheck(String normalizedPath) {
        return normalizedPath.contains("/app/src/main/java/com/omniflow/ui/")
                || normalizedPath.contains("/app/src/main/java/com/omniflow/uicomponents/");
    }

    private static final class ForbiddenPattern {
        private final Pattern pattern;
        private final String message;

        private ForbiddenPattern(Pattern pattern, String message) {
            this.pattern = pattern;
            this.message = message;
        }
    }
}
