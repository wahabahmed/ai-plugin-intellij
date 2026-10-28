package com.turbo.dev.plugin.ui;

import com.android.tools.idea.assistant.DefaultTutorialBundle;
import com.turbo.dev.plugin.dto.scan.Issue;
import com.turbo.dev.plugin.dto.scan.OverAllIssueGroup;
import org.gradle.internal.impldep.org.fusesource.jansi.AnsiRenderer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import static com.turbo.dev.plugin.utils.UiUtils.SYSTEM_COLOR;

public class SummaryReportPanel extends JPanel {
    private final JPanel contentPanel;
    private boolean isExpanded = false;

    public SummaryReportPanel(OverAllIssueGroup group) {
        setAlignmentX(Component.LEFT_ALIGNMENT);
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 1000)); // Allow width growth
        setBackground(SYSTEM_COLOR);

        // Header Panel (Clickable)
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setCursor(new Cursor(Cursor.HAND_CURSOR));
        header.setAlignmentX(LEFT_ALIGNMENT);
        header.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel(group.getType());
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
        titleLabel.setForeground(Color.BLACK);

        JLabel severityLabel = new JLabel(group.getOverAllSeverity());
        severityLabel.setForeground(getSeverityColor(group.getOverAllSeverity()));

        header.add(titleLabel, BorderLayout.WEST);
        header.add(severityLabel, BorderLayout.EAST);
        header.setPreferredSize(new Dimension(275, 50));

        // Content Panel (Holds individual issues)
        contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setVisible(false);
        contentPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.setBackground(SYSTEM_COLOR);

        for (Issue issue : group.getIssues()) {
            JPanel issueItem = createIssueItem(issue);
            issueItem.setFocusable(false);
            // Ensure the child is ALSO left-aligned
            issueItem.setAlignmentX(Component.LEFT_ALIGNMENT);
            contentPanel.add(issueItem);
            contentPanel.add(Box.createRigidArea(new Dimension(0, 10)));
            contentPanel.setFocusable(false);
        }

        header.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                isExpanded = !isExpanded;
                contentPanel.setVisible(isExpanded);

                // 1. Refresh this specific component
                revalidate();
                repaint();

                // 2. Walk up the tree to update the bubble and the chat area
                Component current = SummaryReportPanel.this;
                while (current != null) {
                    if (current instanceof Container) {
                        Container container = (Container) current;

                        // Force layout recalculation
                        container.revalidate();
                        container.repaint();

                        // If this is one of your message rows, we must update the max height limit
                        // so the scroll pane knows the bubble got taller
                        if (container.getLayout() instanceof BoxLayout) {
                            container.setMaximumSize(new Dimension(
                                    Integer.MAX_VALUE,
                                    container.getPreferredSize().height
                            ));
                        }
                    }
                    current = current.getParent();
                }
            }
        });

        SwingUtilities.invokeLater(() -> {
            // This scrolls the clicked section into view if it was cut off
            scrollRectToVisible(getBounds());
        });
        add(header, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);
    }

    private JPanel createIssueItem(Issue issue) {
        JPanel p = new JPanel(new BorderLayout(0, 0));
        p.setBorder(BorderFactory.createMatteBorder(0, 3, 0, 0, Color.RED)); // Red accent line


        // The 'width: 250px' is the key to wrapping.
        // Adjust this number based on your ChatPanel's width.
        String html = String.format(
                "<html><div style='width: 300px; margin: 0; padding: 0; text-align: left;'>" +
                        "<b style='color: #7f8c8d;'>%s</b><br/>" +
                        "<span style='color: #7f8c8d;'>%s</span><br/>" +
                        "<code style='background-color: #eeeeee; padding: 2px;'>%s</code><br/>" +
                        "<small style='color: #7f8c8d;'>%s</small>" +
                        "</div></html>",
                issue.getSeverity(),
                issue.getMessage(),
                issue.getCodeWithLineNumber(),
                issue.getSolution()
        );

        JEditorPane editorPane = new JEditorPane();
        editorPane.setContentType("text/html");
        editorPane.setText(html);

        // Configuration for selector behavior
        editorPane.setEditable(false);
        editorPane.setOpaque(false);
        editorPane.setFocusable(true);

        // Enforce the layout system look and feel fonts/colors over standard HTML styles
        editorPane.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, Boolean.TRUE);
        editorPane.setFont(UIManager.getFont("Label.font"));
        editorPane.setForeground(UIManager.getColor("Label.foreground"));

        editorPane.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.add(editorPane, BorderLayout.CENTER);

        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, p.getPreferredSize().height));
        p.setAlignmentX(Component.LEFT_ALIGNMENT);
        return p;
    }

    private Color getSeverityColor(String sev) {
        if (sev == null) return Color.BLACK;
        switch (sev.toUpperCase()) {
            case "CRITICAL": return new Color(192, 57, 43); // Dark Red
            case "HIGH": return new Color(211, 84, 0);     // Orange
            default: return Color.DARK_GRAY;
        }
    }
}

