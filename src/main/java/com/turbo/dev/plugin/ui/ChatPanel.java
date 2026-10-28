package com.turbo.dev.plugin.ui;

import com.turbo.dev.plugin.dto.ScanResult;
import com.turbo.dev.plugin.dto.chat.ChatRequest;
import com.turbo.dev.plugin.dto.chat.ChatResponse;
import com.turbo.dev.plugin.dto.scan.ScanRequest;
import com.turbo.dev.plugin.dto.subscription.SubscriptionPayment;
import com.turbo.dev.plugin.utils.*;
import com.turbo.dev.plugin.utils.ui.PaymentPlanUiUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;

import static com.turbo.dev.plugin.client.ApiClient.*;
import static com.turbo.dev.plugin.utils.ui.ImageUiUtils.prepareDiagram;
import static com.turbo.dev.plugin.utils.ui.MessageUiUtils.addMessage;
import static com.turbo.dev.plugin.utils.FilesScanner.getScanRequest;
import static com.turbo.dev.plugin.utils.UiUtils.getScaledIcon;
import static com.turbo.dev.plugin.utils.ui.MessageUiUtils.addToChat;
import static com.turbo.dev.plugin.utils.ui.ReportUiUtils.addScanReport;

public class ChatPanel {
    private JPanel panel;
    private JPanel chatArea;
    private JScrollPane scrollPane;
    private JTextArea inputField;
    private MainPanel mainPanel;

    public ChatPanel(MainPanel mainPanel) {
        this.mainPanel = mainPanel;
        panel = new JPanel(new BorderLayout());

        // Top bar
        JPanel topBar = new JPanel(new BorderLayout());
        JButton scanBtn = UiUtils.getSecondaryButton("Scan Project");
        JButton architectureBtn = UiUtils.getSecondaryButton("Project Architecture");
        JButton logoutBtn = UiUtils.getSecondaryButton("Logout");
        JLabel title = new JLabel("AI Plugin", getScaledIcon(getClass()), JLabel.LEFT);
        title.setIconTextGap(8);

        JPanel rightButtons = new JPanel();
        rightButtons.add(scanBtn);
        rightButtons.add(architectureBtn);
        rightButtons.add(logoutBtn);

        topBar.add(title, BorderLayout.WEST);
        topBar.add(rightButtons, BorderLayout.EAST);

        chatArea = new JPanel();
        chatArea.setLayout(new BoxLayout(chatArea, BoxLayout.Y_AXIS));

        scrollPane = new JScrollPane(
                chatArea,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED
        );
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        // Bottom input
        JPanel bottom = new JPanel(new BorderLayout());

        // Chat will have three lines and will allow to have actual context
        inputField = new JTextArea(3, 20);
        inputField.setLineWrap(true);
        inputField.setWrapStyleWord(true);
        JScrollPane inputScroll = new JScrollPane(inputField);
        int lineHeight = inputField.getFontMetrics(inputField.getFont()).getHeight();
        int maxHeight = lineHeight * 3 + 10; // ~3 lines + padding

        inputScroll.setPreferredSize(new Dimension(0, maxHeight));
        inputScroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, maxHeight));
        inputScroll.setMinimumSize(new Dimension(0, maxHeight));
        inputScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        inputScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        JButton sendBtn = UiUtils.getPrimaryButton("Ask Ai!");

        bottom.add(inputScroll, BorderLayout.CENTER);
        bottom.add(sendBtn, BorderLayout.EAST);
        panel.add(topBar, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(bottom, BorderLayout.SOUTH);

        logoutBtn.addActionListener(e -> mainPanel.logoutUser());

        inputField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER && e.isShiftDown()) {
                    sendBtn.doClick();
                    e.consume();
                }
            }
        });
        sendBtn.addActionListener(e -> {
            String message = inputField.getText().trim();

            if (!message.isEmpty()) {
                if (!TokenStorage.isTokenExpired()) {
                    addMessage(chatArea, scrollPane, "``` \n" + message + " \n ```", true, 0);
                    // Example bot response
                    addMessage(chatArea, scrollPane, "Got it — let me think ...", false, 0);
                    inputField.setText("");
                    ChatRequest request = ChatRequest.builder().prompt(message).build();
                    new Thread(() -> {
                    SwingUtilities.invokeLater(() -> {
                        Object response = processAiPrompt(request);
                        if (response instanceof ChatResponse) {
                            addMessage(chatArea, scrollPane, ((ChatResponse) response).getPromptResponses().get(0), false, 0);
                        } else {
                            loadPaymentPlans((List<SubscriptionPayment>) response);
                        }
                    });
                    }).start();
                } else {
                    mainPanel.showLogin();
                }
            }
        });

        scanBtn.addActionListener(e -> callApiToProcessProjectForReporting());
        architectureBtn.addActionListener(e -> callAiToProcessProjectArchitectureDiagrams());
    }

    private void loadPaymentPlans(List<SubscriptionPayment> plans) {
        addMessage(chatArea, scrollPane, "💳 You have reached your usage limits. Please update/renew your plan.", false, 0);
        try {
            JPanel ui = PaymentPlanUiUtils.createPlans(plans);
            addToChat(chatArea, scrollPane,ui, false);
        } catch (Exception e) {
            addMessage(chatArea, scrollPane, "Error loading plans: " + e.getMessage(), false, 0);
        }
    }

    private void callApiToProcessProjectForReporting() {
        addMessage(chatArea, scrollPane, "\uD83D\uDD0D Scanning project...", false, 0);
        try {
            ScanRequest request = getScanRequest();

            if (!TokenStorage.isTokenExpired()) {
                addMessage(chatArea, scrollPane, String.format("I am analyzing %s Files", request.getTotalFiles()), false, 0);

                new Thread(() -> {
                    Object response = sendScanToBackend(request);
                    if (response instanceof ScanResult) {
                        addScanReport(chatArea, scrollPane, (ScanResult) response);
                    } else {
                        loadPaymentPlans((List<SubscriptionPayment>) response);
                    }
                }).start();
            } else {
                mainPanel.showLogin();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void callAiToProcessProjectArchitectureDiagrams() {
        addMessage(chatArea, scrollPane, "\uD83D\uDD0D Scanning project...", false, 0);
        try {
            ScanRequest request = getScanRequest();
            if (!TokenStorage.isTokenExpired()) {
                addMessage(chatArea, scrollPane, String.format("I am analyzing %s Files", request.getTotalFiles()), false, 0);
                new Thread(() -> {
                    List<?> response = sendArchitectureToBackend(request);
                    if (!response.isEmpty() && response.get(0) instanceof String) {
                        for (String item : (List<String>) response) {
                            SwingUtilities.invokeLater(() -> {
                                if (item.contains("Here is your project documentation (Markdown)")) {
                                    addMessage(chatArea, scrollPane, item, false, 40);
                                } else {
                                    prepareDiagram(chatArea, scrollPane, item);
                                }
                            });

                            try {
                                Thread.sleep(500);
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    } else {
                        loadPaymentPlans((List<SubscriptionPayment>) response);
                    }
                }).start();
            } else {
                mainPanel.showLogin();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public JPanel getPanel() {
        return panel;
    }
}