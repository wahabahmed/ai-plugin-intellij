package com.turbo.dev.plugin.utils.ui;

import com.turbo.dev.plugin.ui.RoundedBubble;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;

import com.vladsch.flexmark.util.data.MutableDataSet;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

import static com.turbo.dev.plugin.utils.UiUtils.SYSTEM_COLOR;
import static com.turbo.dev.plugin.utils.UiUtils.USER_COLOR;

public class MessageUiUtils {
    /**
     * This method will create a chat bubble and then add into the chat area.
     * User input will show on right whereas robot input will show on left
     *
     * @param chatArea   is the JPanel that has chat ui
     * @param scrollPane is the JScrollPane that will help us to handle vertical scroll
     * @param text       is the data that we will show in a bubble
     * @param isUser
     */
    public static void addMessage(JPanel chatArea, JScrollPane scrollPane, String text, boolean isUser, int bottomHeight) {
        // OUTER ROW (full width)
        JPanel messageWrapper = new JPanel();
        messageWrapper.setLayout(new BoxLayout(messageWrapper, BoxLayout.Y_AXIS));
        messageWrapper.setOpaque(false);

        // ALIGN LEFT or RIGHT
        JPanel alignPanel = new JPanel(new FlowLayout(isUser ? FlowLayout.RIGHT : FlowLayout.LEFT));
        alignPanel.setOpaque(false);

        // BUBBLE
        RoundedBubble bubble = new RoundedBubble(
                isUser ? USER_COLOR : SYSTEM_COLOR
        );
        bubble.setLayout(new BorderLayout());

        // Ensures the JTextPane fills the bubble
        String html = getTextHtmlString(text, isUser, chatArea.getWidth());
        JTextPane messageLabel = new JTextPane();
        messageLabel.setContentType("text/html");
        messageLabel.setText(html);
        messageLabel.setEditable(false);
        messageLabel.setOpaque(false);
        messageLabel.setForeground(isUser ? Color.WHITE : Color.BLACK);
        // Crucial: Set a maximum width directly on the text editor kit to force HTML text wrapping at 400px
        messageLabel.setSize(new Dimension(400, Integer.MAX_VALUE));
        messageLabel.setForeground(isUser ? Color.WHITE : Color.BLACK);
        bubble.add(messageLabel, BorderLayout.CENTER);
        messageLabel.putClientProperty(JTextPane.HONOR_DISPLAY_PROPERTIES, Boolean.TRUE);
        // Refresh Style for html
        messageLabel.getUI().getPreferredSize(messageLabel);

        // DYNAMIC SIZING FIX: Override getPreferredSize to enforce 200px min and 400px max
        Dimension labelPrefSize = messageLabel.getPreferredSize();
        int boundedWidth = Math.max(200, Math.min(400, labelPrefSize.width));

        // Recalculate preferred height based on the new bounded width constraint
        messageLabel.setSize(new Dimension(boundedWidth, Integer.MAX_VALUE));
        if (bottomHeight == 0) bottomHeight = 20;
        int finalHeight = messageLabel.getPreferredSize().height + bottomHeight; // Add padding for bubble borders

        bubble.setPreferredSize(new Dimension(boundedWidth, finalHeight));

        alignPanel.add(bubble);
        messageWrapper.add(alignPanel);

        // Keep wrapper full-width for the BoxLayout vertical stacking
        messageWrapper.setMaximumSize(new Dimension(Integer.MAX_VALUE, messageWrapper.getPreferredSize().height));

        chatArea.add(messageWrapper);
        chatArea.revalidate();
        chatArea.repaint();

        // AUTO SCROLL
        SwingUtilities.invokeLater(() -> scrollPane.getVerticalScrollBar().setValue(
                scrollPane.getVerticalScrollBar().getMaximum()
        ));
    }

    /**
     * This method will handle look and feel of a bubble message
     *
     * @param text
     * @param isUser
     * @return
     */
    private static @NotNull String getTextHtmlString(String text, boolean isUser, int currentWidth) {
        Parser parser;
        HtmlRenderer renderer;
        MutableDataSet options = new MutableDataSet();
        parser = Parser.builder(options).build();
        renderer = HtmlRenderer.builder(options).build();

        String renderedText = text;

        String cleanMarkdown = text
                .replace("<br>", "\n")
                .replace("<br/>", "\n")
                .replace("<br />", "\n");
            renderedText = renderer.render(parser.parse(cleanMarkdown));
            renderedText = renderedText
                    .replace("<p>", "")
                    .replace("</p>", "");

        String colorHex = isUser ? "#FFFFFF" : "#000000";
        String codeHex = isUser ? "#FFFFFF" : "#A52A2A";
        String preBackground = isUser ? "transparent" : "#f4f4f4";
        String style = "<style>" +
                "body { margin: 0; padding: 0px; font-size: 11px; color: " + colorHex + ";}" +
                "pre { white-space: pre-wrap; background: " + preBackground + "; padding: 8px}" + // FIXED: Added missing colon ':' here
                "code { font-family: Arial;  color: " + codeHex +";}" +
                "</style>";
        String html = "<html>" + style + "<body>" +
                "<table width='100%' style='table-layout: fixed;'>" +
                "<tr>" +
                    "<td style=' word-wrap: break-word; word-break: break-all;'>" +
                 renderedText +
                "</td>" +
                "</tr>" +
                "</table>" +
                "</body></html>";

        System.out.println("rendered text " + renderedText);


        return html.trim();
    }

    private static String generateHtmlDocument(String renderedHtml) {
        // 1. Define the HTML structure exactly using a Java Text Block
        String htmlTemplate = """
        <!DOCTYPE html>
        <html lang="en">
        <head>
            <meta charset="UTF-8">
            <!-- Include a Prism CSS theme (e.g., Tomorrow Night) -->
            <link rel="stylesheet" href="https://cloudflare.com" />
            <style>
                body {
                    font-family: var(--vscode-font-family);
                    color: var(--vscode-editor-foreground);
                    background-color: var(--vscode-editor-background);
                    padding: 15px;
                    line-height: 1.6;
                }
                pre {
                    background: #1e1e1e;
                    border-radius: 4px;
                    padding: 10px;
                    overflow-x: auto;
                    position: relative;
                }
                code {
                    font-family: var(--vscode-editor-font-family);
                }
            </style>
        </head>
        <body>
            <div id="content">%s</div>
        </body>
        </html>
        """;

        // 2. Inject your rendered HTML string into the %s placeholder safely
        return String.format(htmlTemplate, renderedHtml);
    }

    public static void addToChat(JPanel chatArea, JScrollPane scrollPane, JComponent component, boolean isUser) {

        SwingUtilities.invokeLater(() -> {
            JPanel messageRow = new JPanel(new BorderLayout());
            messageRow.setMaximumSize(new Dimension(800, 400));

            JPanel bubble = new RoundedBubble(
                    isUser ? new Color(220, 240, 255)
                            : new Color(227, 242, 253)
            );

            bubble.setLayout(new BorderLayout());
            bubble.add(component, BorderLayout.CENTER);

            messageRow.add(bubble, isUser ? BorderLayout.EAST : BorderLayout.WEST);

            chatArea.add(messageRow);
            chatArea.add(Box.createVerticalStrut(6));
            chatArea.revalidate();
            chatArea.repaint();

            SwingUtilities.invokeLater(() -> {
                JScrollBar vertical = scrollPane.getVerticalScrollBar();
                vertical.setValue(vertical.getMaximum());
            });
        });
    }
}
