package com.turbo.dev.plugin.utils;

import org.commonmark.ext.gfm.tables.TablesExtension;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import java.util.Collections;

public class DataFormatter {

    private static String classStart(String name, String language) {
        return "<<<CLASS_START name=" + name + " language=" + language + ">>>";
    }

    private static String classEnd(String name) {
        return "<<<CLASS_END name=" + name + ">>>";
    }

    public static String buildDataFormat(String file, String data) {
        String language = detectLang(file);
        String fileName = (file != null && !file.isEmpty()) ? file : "Unknown";
        String lang = (language != null && !language.isEmpty()) ? language : "Unknown";

        StringBuilder sb = new StringBuilder();

        sb.append(classStart(fileName, lang))
                .append("\n")
                .append(data != null ? data : "")
                .append("\n")
                .append(classEnd(fileName))
                .append("\n");

        return sb.toString();
    }

    public static String detectLang(String file) {

        if (file == null) return "unknown";

        file = file.toLowerCase();

        if (file.endsWith(".c")) return "C";
        if (file.endsWith(".cpp") || file.endsWith(".cc")) return "C++";
        if (file.endsWith(".cs")) return "C#";
        if (file.endsWith(".css")) return "CSS";
        if (file.endsWith(".dart")) return "Dart";
        if (file.endsWith(".go")) return "Go";
        if (file.endsWith(".html") || file.endsWith(".htm")) return "HTML";
        if (file.endsWith(".java")) return "Java";
        if (file.endsWith(".js")) return "JavaScript";
        if (file.endsWith(".json")) return "JSON";
        if (file.endsWith(".kt") || file.endsWith(".kts")) return "Kotlin";
        if (file.endsWith(".lua")) return "Lua";
        if (file.endsWith(".md")) return "Markdown";
        if (file.endsWith(".m")) return "Objective-C";
        if (file.endsWith(".php")) return "PHP";
        if (file.endsWith(".py")) return "Python";
        if (file.endsWith(".rb")) return "Ruby";
        if (file.endsWith(".rs")) return "Rust";
        if (file.endsWith(".scala")) return "Scala";
        if (file.endsWith(".sh")) return "Shell";
        if (file.endsWith(".sql")) return "SQL";
        if (file.endsWith(".swift")) return "Swift";
        if (file.endsWith(".ts")) return "TypeScript";
        if (file.endsWith(".yaml") || file.endsWith(".yml")) return "YAML";
        if (file.endsWith(".gradle")) return "Gradle";

        if (file.endsWith("pom.xml")) return "MVN";
        if (file.endsWith(".xml")) return "XML";

        return "unknown";
    }

    public static String formatChatResponseToHtml(String markdown) {
        // Enable GitHub Tables extension
        var extensions = Collections.singletonList(TablesExtension.create());

        Parser parser = Parser.builder()
                .extensions(extensions)
                .build();

        HtmlRenderer renderer = HtmlRenderer.builder()
                .extensions(extensions)
                .build();

        Node document = parser.parse(markdown);
        return renderer.render(document);
    }
}
