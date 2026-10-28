package com.turbo.dev.plugin.factory;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.JBTextArea;
import com.intellij.ui.content.ContentFactory;
import com.turbo.dev.plugin.ui.MainPanel;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

import com.intellij.ui.content.Content;

public class TurboDevChatFactory implements ToolWindowFactory {
    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        JPanel panel = new JPanel(new BorderLayout());

        /*JBTextArea chatArea = new JBTextArea();
        chatArea.setEditable(false);

        JTextField inputField = new JTextField();

        // Action when user presses Enter
        inputField.addActionListener(e -> {
            String userText = inputField.getText();
            chatArea.append("You: " + userText + "\n");

            // Logic to send to AI would go here
            chatArea.append("AI: Processing...\n");

            inputField.setText("");
        });

        panel.add(new JBScrollPane(chatArea), BorderLayout.CENTER);
        panel.add(inputField, BorderLayout.SOUTH);*/

        MainPanel mainPanel = new MainPanel();

        Content content =
                ContentFactory.getInstance().createContent(mainPanel.getMainPanel(), "", false);

        toolWindow.getContentManager().addContent(content);

    }
}
