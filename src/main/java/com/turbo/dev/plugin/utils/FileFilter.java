package com.turbo.dev.plugin.utils;

import java.util.*;
import java.util.regex.Pattern;

public class FileFilter {

    // ======================
    // Extensions
    // ======================
    private static final Set<String> ALLOWED_EXTENSIONS = new HashSet<>(Arrays.asList(
            ".js", ".mjs", ".cjs", ".jsx",
            ".ts", ".tsx",

            ".html", ".htm",
            ".css", ".scss", ".sass", ".less",
            ".vue", ".svelte", ".astro",

            ".java", ".kt", ".kts",
            ".gradle",
            ".xml",

            ".swift",
            ".m", ".mm",
            ".h",
            ".xib", ".storyboard",
            ".plist",
            ".pbxproj",

            ".c", ".cpp", ".cc", ".cxx",
            ".hpp", ".hh", ".hxx",

            ".rs",
            ".go",
            ".zig",
            ".nim",

            ".cs",
            ".fs", ".fsx",
            ".vb",
            ".csproj", ".fsproj", ".vbproj",
            ".sln",

            ".py", ".pyw",
            ".rb",
            ".php",
            ".pl", ".pm",
            ".lua",
            ".tcl",

            ".sh", ".bash", ".zsh", ".fish",
            ".ps1",

            ".json", ".jsonc",
            ".yaml", ".yml",
            ".toml",
            ".ini", ".cfg", ".conf",
            ".env",
            ".properties",

            ".dockerignore",
            ".tf", ".tfvars",
            ".hcl",

            ".sql",

            ".md", ".mdx",
            ".rst",
            ".txt",

            ".cmake",
            ".make",
            ".mk",

            ".graphql", ".gql",

            ".lock",
            ".log"
    ));

    // ======================
    // Filenames
    // ======================
    private static final Set<String> ALLOWED_FILENAMES = new HashSet<>(Arrays.asList(
            "Dockerfile",
            "Makefile",
            "CMakeLists.txt",
            "Procfile",
            ".gitignore",
            ".gitattributes",
            ".editorconfig",
            "package.json"
    ));

    // ======================
    // Patterns
    // ======================
    private static final List<Pattern> ALLOWED_PATTERNS = Arrays.asList(
            Pattern.compile("^\\.env\\..+$"),
            Pattern.compile("^Dockerfile\\..+$"),
            Pattern.compile("^.*\\.config\\.(js|ts|mjs|cjs)$")
    );

    // ======================
    // Excluded directories
    // ======================
    private static final Set<String> EXCLUDED_DIRS = new HashSet<>(Arrays.asList(
            "node_modules",
            "dist",
            "build",
            "out",
            "bin",
            "target",
            ".next",
            ".nuxt",
            ".output",
            ".turbo",
            ".mvn",
            ".gradle",

            ".git",
            ".svn",
            ".hg",

            ".vscode",
            ".idea",

            "logs",
            "tmp",
            "temp",
            ".cache",

            "coverage",

            "__pycache__",
            ".pytest_cache",

            ".DS_Store"
    ));

    // ======================
    // Excluded path patterns
    // ======================
    private static final List<Pattern> EXCLUDED_PATH_PATTERNS = Arrays.asList(
            Pattern.compile(".*/node_modules/.*"),
            Pattern.compile(".*/dist/.*"),
            Pattern.compile(".*/build/.*"),
            Pattern.compile(".*/target/.*")
    );

    // ======================
    // Helpers
    // ======================

    private static boolean isInExcludedDir(String filePath) {
        String[] parts = filePath.split("[/\\\\]");

        for (String part : parts) {
            if (EXCLUDED_DIRS.contains(part)) {
                return true;
            }
        }

        for (Pattern p : EXCLUDED_PATH_PATTERNS) {
            if (p.matcher(filePath).find()) {
                return true;
            }
        }

        return false;
    }

    // ======================
    // MAIN FUNCTION
    // ======================
    public static boolean isAllowedFile(String filePath) {

        if (filePath == null) return false;

        String fileName = filePath.substring(
                filePath.lastIndexOf('/') + 1
        );

        String ext = "";

        int dot = fileName.lastIndexOf('.');
        if (dot > 0) {
            ext = fileName.substring(dot).toLowerCase();
        }

        // 1. Excluded directories first
        if (isInExcludedDir(filePath)) {
            return false;
        }

        // 2. Extension check
        if (!ext.isEmpty() && ALLOWED_EXTENSIONS.contains(ext)) {
            return true;
        }

        // 3. Exact filename
        if (ALLOWED_FILENAMES.contains(fileName)) {
            return true;
        }

        // 4. Pattern match
        for (Pattern pattern : ALLOWED_PATTERNS) {
            if (pattern.matcher(fileName).matches()) {
                return true;
            }
        }

        return false;
    }
}
