package com.turbo.dev.plugin.utils.ui;

import com.turbo.dev.plugin.dto.ScanResult;
import com.turbo.dev.plugin.dto.scan.OverAllIssueGroup;
import com.turbo.dev.plugin.ui.RoundedBubble;
import com.turbo.dev.plugin.ui.SummaryReportPanel;

import javax.swing.*;
import java.awt.*;

import static com.turbo.dev.plugin.utils.UiUtils.SYSTEM_COLOR;

public class ReportUiUtils {
    public static void addScanReport(JPanel chatArea, JScrollPane scrollPane,  ScanResult result) {
        // 1. OUTER WRAPPER (To align left like a bot message)
        JPanel alignPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        alignPanel.setOpaque(false);

        // 2. THE BUBBLE (Reuse your RoundedBubble class)
        // Color (230, 230, 230) is what you used for bot messages in addMessage
        RoundedBubble bubble = new RoundedBubble(SYSTEM_COLOR);
        bubble.setLayout(new BoxLayout(bubble, BoxLayout.Y_AXIS));

        // 3. HEADER (Quick Summary)
        String color = getStatusColor(result.getOverAllStatus());
        String formattedStatus = formatStatus(result.getOverAllStatus());
        String summaryHtml = String.format(
                "<html>" +
                        "<table width='200px' border='0' cellspacing='0' cellpadding='0'>" +
                        "  <tr>" +
                        "    <td width='100px'><h3 style='margin-bottom:10px;'>🛡️ Quick Summary</h3></td>" +
                        "    <td align='right'><span style='color:%s; font-size: 14pt;'>●</span></td>" +
                        "  </tr>" +
                        "</table>" +
                        "<p style='margin-top:5x;'><b>Status:</b> %s</p>" +
                        "<p style='margin-top:5px;margin-bottom:5px;'><b>Score:</b> %d%%</p>" +
                        "</html>",
                color, formattedStatus, result.getOverallScore()
        );
        JLabel summaryLabel = new JLabel(summaryHtml);
        summaryLabel.setForeground(Color.BLACK);
        summaryLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        bubble.add(summaryLabel);

        // 4. ADD ISSUE GROUPS
        if (result.getOverAllIssues() != null) {
            for (OverAllIssueGroup group : result.getOverAllIssues()) {
                SummaryReportPanel groupPanel = new SummaryReportPanel(group);
                // Make the internal panel transparent so bubble color shows through
                //groupPanel.setOpaque(false);
                bubble.add(groupPanel);
                bubble.add(Box.createRigidArea(new Dimension(0, 8)));
            }
        }

        // 5. ADD TO CHAT AREA
        alignPanel.add(bubble);

        // Wrap in one more box to prevent the bubble from stretching vertically
        JPanel rowWrapper = new JPanel();
        rowWrapper.setLayout(new BoxLayout(rowWrapper, BoxLayout.Y_AXIS));
        rowWrapper.setOpaque(false);
        rowWrapper.add(alignPanel);
        rowWrapper.setMaximumSize(new Dimension(Integer.MAX_VALUE, rowWrapper.getPreferredSize().height));

        chatArea.add(rowWrapper);

        // 6. REFRESH & SCROLL
        SwingUtilities.invokeLater(() -> {
            chatArea.revalidate();
            chatArea.repaint();
            scrollPane.getVerticalScrollBar().setValue(scrollPane.getVerticalScrollBar().getMaximum());
        });
    }

    /**
     * Maps the status string to a hex color code for HTML styling.
     * Equivalent to your JavaScript getStatusColor function.
     */
    private static String getStatusColor(String status) {
        // Handle null or empty with the fallback grey
        if (status == null || status.isEmpty()) {
            return "#95a5a6";
        }

        switch (status.toUpperCase()) {
            case "READY":
                return "#2ecc71"; // green
            case "ALMOST_READY":
                return "#f39c12"; // orange
            case "NOT_READY":
                return "#e74c3c"; // red
            default:
                return "#95a5a6"; // grey fallback
        }
    }

    /**
     * Formats status strings (e.g., "NOT_READY" -> "Not Ready").
     * Equivalent to your JavaScript formatStatus function.
     */
    private static String formatStatus(String status) {
        if (status == null || status.isEmpty()) {
            return "";
        }

        // 1. Replace underscores and convert to lowercase
        String input = status.replace("_", " ").toLowerCase();

        // 2. Capitalize first letter of each word (Title Case)
        StringBuilder result = new StringBuilder();
        for (String word : input.split(" ")) {
            if (!word.isEmpty()) {
                result.append(Character.toUpperCase(word.charAt(0)))
                        .append(word.substring(1))
                        .append(" ");
            }
        }

        return result.toString().trim();
    }
}
