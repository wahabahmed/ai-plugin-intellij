package com.turbo.dev.plugin.ui;

import com.google.gson.Gson;
import com.intellij.ide.BrowserUtil;
import com.intellij.ui.components.JBLabel;
import com.intellij.util.ui.JBUI;
import com.sun.net.httpserver.HttpServer;
import com.turbo.dev.plugin.dto.TokenResponse;
import com.turbo.dev.plugin.utils.PluginConstants;
import com.turbo.dev.plugin.utils.TokenStorage;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.*;
import java.nio.charset.StandardCharsets;

import static com.turbo.dev.plugin.client.ApiClient.checkUser;

@Slf4j
public class LoginPanel {
    private JPanel panel;
    private JButton loginButton;
    private MainPanel mainPanel;

    public LoginPanel(MainPanel mainPanel) {
        this.mainPanel = mainPanel;

        panel = new JPanel(new BorderLayout());
        panel.setBackground(UIManager.getColor("Panel.background"));

        RoundedCardPanel card = new RoundedCardPanel(20);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        card.setBorder(JBUI.Borders.empty(20, 20));
        card.setBackground(UIManager.getColor("Editor.background"));
        card.setMaximumSize(new Dimension(100, 320));

        JBLabel titleLabel = new JBLabel("\uD83D\uDD10 TurboDev");
        titleLabel.setFont(new Font(titleLabel.getFont().getName(), Font.BOLD, 24));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JBLabel subtitleLabel = new JBLabel("Code Faster • AI Assist • Debug • Refactor");
        subtitleLabel.setForeground(UIManager.getColor("Label.disabledForeground"));
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton loginButton = new JButton("Login");
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginButton.setMaximumSize(new Dimension(200, 40));
        loginButton.setBackground(new Color(0, 120, 215));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        loginButton.setBorderPainted(false);
        loginButton.setContentAreaFilled(false);
        loginButton.setOpaque(true);

        loginButton.addActionListener(e -> {
            authenticateUser();

        });

        JButton forgotBtn = new JButton("Forgot password?");
        forgotBtn.setBorderPainted(false);
        forgotBtn.setContentAreaFilled(false);
        forgotBtn.setFocusPainted(false);
        forgotBtn.setForeground(UIManager.getColor("Link.activeForeground"));
        forgotBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        forgotBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton signupBtn = new JButton("Signup");
        signupBtn.setBorderPainted(false);
        signupBtn.setContentAreaFilled(false);
        signupBtn.setFocusPainted(false);
        signupBtn.setForeground(UIManager.getColor("Link.activeForeground"));
        signupBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        signupBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        JBLabel loginFooter = new JBLabel("Please contact your administrator if you have not been onboarded");
        loginFooter.setFont(JBUI.Fonts.smallFont());
        loginFooter.setAlignmentX(Component.CENTER_ALIGNMENT);

        JBLabel messageFooter = new JBLabel(
                "<html><div style='width:265px; text-align: center;'>"
                        + "AI-powered code review, security scanning, and risk scoring—built into your IDE so every commit is production-ready. Turn hundreds of issues and warnings into a single, clear status: READY, ALMOST READY or NOT READY." +
                        "</div></html>");
        messageFooter.setFont(JBUI.Fonts.smallFont());
        messageFooter.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(titleLabel);
        card.add(Box.createVerticalStrut(10));
        card.add(subtitleLabel);
        card.add(Box.createVerticalStrut(30));
        card.add(loginButton);
        card.add(Box.createVerticalStrut(10));
        card.add(forgotBtn);
        card.add(signupBtn);
        card.add(Box.createVerticalGlue());
        //card.add(loginFooter);
        card.add(Box.createVerticalStrut(30));
        card.add(Box.createVerticalGlue());
        card.add(messageFooter);

        JPanel center = new JPanel(new GridBagLayout());
        center.setBackground(UIManager.getColor("Panel.background"));
        center.add(card);

        JBLabel copyright = new JBLabel("© 2026 TurboDev AI. All rights reserved.");
        copyright.setFont(JBUI.Fonts.smallFont());
        copyright.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER));
        footer.setBackground(UIManager.getColor("Panel.background"));
        footer.add(copyright);

        panel.add(center, BorderLayout.CENTER);
        panel.add(footer, BorderLayout.SOUTH);
    }

    public void authenticateUser() {
        String authUrl = null;
        try {
            authUrl = PluginConstants.KEYCLOAK_URL_KEY +
                    "/realms/" + PluginConstants.KEYCLOAK_REALM +
                    "/protocol/openid-connect/auth" +
                    "?client_id=" + PluginConstants.KEYCLOAK_CLIENT +
                    "&response_type=code" +
                    "&redirect_uri=" + URLEncoder.encode("http://localhost:8765/callback/login-success", "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }

        log.info("logging in");
        BrowserUtil.browse(authUrl);
        waitForLoginCallback();
        checkUser();
    }

    public void waitForLoginCallback() {
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(8765), 0);
            server.createContext("/callback/login-success", exchange -> {
                String query = exchange.getRequestURI().getQuery();
                String code = extractParam(query, "code");

                String response =
                        """
                                <!DOCTYPE html>
                                                <html>
                                                <head>
                                                    <meta charset="UTF-8">
                                                    <link href="https://googleapis.com" rel="stylesheet">
                                                    <style>
                                                        body {
                                                            margin: 0; padding: 0;
                                                            display: flex; justify-content: center; align-items: center;
                                                            height: 100vh; font-family: 'Inter', sans-serif;
                                                            background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
                                                            color: #1a1a1a;
                                                        }
                                                        .card {
                                                            background: rgba(255, 255, 255, 0.9);
                                                            backdrop-filter: blur(10px);
                                                            padding: 40px 60px;
                                                            border-radius: 24px;
                                                            box-shadow: 0 20px 40px rgba(0,0,0,0.1);
                                                            text-align: center;
                                                            max-width: 400px;
                                                            animation: slideUp 0.6s ease-out;
                                                        }
                                                        @keyframes slideUp {
                                                            from { opacity: 0; transform: translateY(20px); }
                                                            to { opacity: 1; transform: translateY(0); }
                                                        }
                                                        .icon {
                                                            width: 64px; height: 64px;
                                                            background: #00d26a; color: white;
                                                            border-radius: 50%; display: flex;
                                                            align-items: center; justify-content: center;
                                                            margin: 0 auto 20px; font-size: 32px;
                                                        }
                                                        h1 { margin: 0 0 12px; font-weight: 600; font-size: 24px; }
                                                        p { color: #666; line-height: 1.5; margin-bottom: 24px; }
                                                        .status {
                                                            font-size: 12px; font-weight: 600;
                                                            text-transform: uppercase; letter-spacing: 1px;
                                                            color: #00d26a; background: rgba(0, 210, 106, 0.1);
                                                            padding: 6px 12px; border-radius: 100px;
                                                            display: inline-block;
                                                        }
                                                    </style>
                                                </head>
                                                <body>
                                                    <div class="card">
                                                        <div class="icon">✓</div>
                                                        <div class="status">Authenticated</div>
                                                        <h1>Login Successful</h1>
                                                        <p>TurboDev AI is now linked. You can safely close this window and return to IntelliJ.</p>
                                                    </div>
                                                    <script>
                                                        // Optional: Auto-close tab after 3 seconds
                                                        setTimeout(() => { window.close(); }, 3000);
                                                    </script>
                                                </body>
                                                </html>                              
                                """;

                byte[] responseBytes = response.getBytes(StandardCharsets.UTF_8);
                exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
                exchange.sendResponseHeaders(200, responseBytes.length);
                exchange.getResponseBody().write(response.getBytes());
                exchange.close();

                // Exchange code for token
                handleTokenExchange(code);

                server.stop(0);
            });

            server.start();
        } catch (Exception e) {
            log.info("call back is already in use {}", e.getMessage());
        }
    }

    private String extractParam(String query, String key) {
        if (query == null || query.isEmpty()) {
            return null;
        }

        String[] params = query.split("&");

        for (String param : params) {
            String[] pair = param.split("=");

            if (pair.length == 2 && pair[0].equals(key)) {
                return URLDecoder.decode(pair[1], StandardCharsets.UTF_8);
            }
        }

        return null;
    }

    public void handleTokenExchange(String code) {
        try {
            System.out.println("handling ");
            URL url = null;
            try {
                url = new URL(PluginConstants.KEYCLOAK_URL_KEY +
                        "/realms/" + PluginConstants.KEYCLOAK_REALM +
                        "/protocol/openid-connect/token");
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }

            HttpURLConnection conn = null;
            conn = (HttpURLConnection) url.openConnection();


            conn.setRequestMethod("POST");

            conn.setDoOutput(true);

            String params =
                    "grant_type=authorization_code" +
                            "&client_id=" + PluginConstants.KEYCLOAK_CLIENT +
                            "&code=" + code +
                            "&redirect_uri=" + URLEncoder.encode("http://localhost:8765/callback/login-success", "UTF-8");

            try (OutputStream os = conn.getOutputStream()) {
                os.write(params.getBytes());
            }

            InputStream is = conn.getInputStream();
            String response = new String(is.readAllBytes());

            Gson gson = new Gson();
            TokenResponse token = gson.fromJson(response, TokenResponse.class);
            TokenStorage.setAccessToken(token);
            log.info("login successful");

            if (token.getAccessToken() != null) {
                mainPanel.showChat();
            }
        } catch (Exception e) {
            log.error("An error occurred in Authentication {}", e.getMessage());
        }
    }

    public JPanel getPanel() {
        return panel;
    }
}
