package com.turbo.dev.plugin.utils;

import java.util.regex.Pattern;

public class CodeSanitizer {

    // Precompiled patterns (faster than inline regex)
    private static final Pattern EMPTY_LINES =
            Pattern.compile("(?m)^\\s*[\\r\\n]");

    private static final Pattern MULTIPLE_BLANK_LINES =
            Pattern.compile("\\n{2,}");

    private static final Pattern PY_BASH_RUBY_COMMENTS =
            Pattern.compile("(?m)^\\s*#.*$");

    private static final Pattern LINE_COMMENTS =
            Pattern.compile("//.*$");

    private static final Pattern BLOCK_COMMENTS =
            Pattern.compile("/\\*[\\s\\S]*?\\*/");

    private static final Pattern JAVA_PRINTLN =
            Pattern.compile("\\bSystem\\.out\\.println\\s*\\(.*?\\)\\s*;?");

    private static final Pattern LOGGING =
            Pattern.compile("\\blog\\.(info|debug|error|warn)\\s*\\(.*?\\)\\s*;?");

    private static final Pattern PY_PRINT =
            Pattern.compile("\\bprint\\s*\\(.*?\\)\\s*");

    private static final Pattern CONSOLE_LOGS =
            Pattern.compile("\\bconsole\\.(log|info|debug|error|warn)\\s*\\(.*?\\)\\s*;?");

    private static final Pattern WHITESPACE =
            Pattern.compile("[ \\t]+");

    public static String sanitizeCode(String code) {

        if (code == null) return "";

        String result = code;

        // Remove empty/whitespace-only lines
        result = EMPTY_LINES.matcher(result).replaceAll("");

        // Collapse multiple blank lines
        result = MULTIPLE_BLANK_LINES.matcher(result).replaceAll("\n");

        // Remove # comments
        result = PY_BASH_RUBY_COMMENTS.matcher(result).replaceAll("");

        // Remove // comments
        result = LINE_COMMENTS.matcher(result).replaceAll("");

        // Remove block comments (/* */)
        result = BLOCK_COMMENTS.matcher(result).replaceAll(match ->
                match.group().replaceAll("[^\\n]", "")
        );

        // Remove logging / prints
        result = JAVA_PRINTLN.matcher(result).replaceAll("");
        result = LOGGING.matcher(result).replaceAll("");
        result = PY_PRINT.matcher(result).replaceAll("");
        result = CONSOLE_LOGS.matcher(result).replaceAll("");

        // Normalize horizontal whitespace
        result = WHITESPACE.matcher(result).replaceAll(" ");

        return result.trim();
    }
}
