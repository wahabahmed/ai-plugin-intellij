package com.turbo.dev.plugin.ui;

import com.intellij.ide.BrowserUtil;
import com.intellij.openapi.application.ApplicationManager;
import com.sun.net.httpserver.HttpServer;
import com.turbo.dev.plugin.utils.TokenStorage;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;
import java.net.InetSocketAddress;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import static com.turbo.dev.plugin.utils.PluginConstants.*;

@Slf4j
public class MainPanel {
    private JPanel mainPanel;
    private CardLayout cardLayout;

    private LoginPanel loginPanel;
    private ChatPanel chatPanel;

    public MainPanel() {
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        loginPanel = new LoginPanel(this);
        chatPanel = new ChatPanel(this);

        mainPanel.add(loginPanel.getPanel(), "LOGIN");
        mainPanel.add(chatPanel.getPanel(), "CHAT");

        String token = TokenStorage.getAccessToken();
        if (null != token && token.length() > 1) {
            showChat();
        } else {
            showLogin();
        }
    }

    public JPanel getMainPanel() {
        return mainPanel;
    }

    public void showLogin() {
        cardLayout.show(mainPanel, "LOGIN");
    }

    public void logoutUser() {
        try {

            // 2. Build the Logout URL
            String redirectUri = URLEncoder.encode("http://localhost:8765/callback/logout-success", StandardCharsets.UTF_8);
            String logoutUrl = KEYCLOAK_URL_KEY + "/realms/" + KEYCLOAK_REALM + "/protocol/openid-connect/logout" +
                    "?post_logout_redirect_uri=" + redirectUri +
                    "&client_id=" + KEYCLOAK_CLIENT;

            // 3. Open browser (Equivalent to vscode.env.openExternal)
            BrowserUtil.browse(logoutUrl);
            waitForLoginCallback();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void waitForLoginCallback() {
        try {
            ApplicationManager.getApplication().invokeLater(() -> {
                TokenStorage.removeAccessToken();
                this.showLogin(); // Now this will work
            });

            HttpServer server = HttpServer.create(new InetSocketAddress(8765), 0);
            server.createContext("/callback/logout-success", exchange -> {
                TokenStorage.removeAccessToken();
                this.showLogin();

                String response =
                        """
                                `
                                            <!DOCTYPE html>
                                            <html lang="en">
                                            <head>
                                                <meta charset="UTF-8">
                                                <link href="https://googleapis.com" rel="stylesheet">
                                                <style>
                                                    body {
                                                        margin: 0; padding: 0;
                                                        display: flex; justify-content: center; align-items: center;
                                                        height: 100vh; font-family: 'Inter', sans-serif;
                                                        background: linear-gradient(135deg, #ece9e6 0%, #ffffff 100%);
                                                        color: #1a1a1a;
                                                    }
                                                    .card {
                                                        background: rgba(255, 255, 255, 0.9);
                                                        backdrop-filter: blur(10px);
                                                        padding: 40px 60px;
                                                        border-radius: 24px;
                                                        box-shadow: 0 20px 40px rgba(0,0,0,0.05);
                                                        text-align: center;
                                                        max-width: 400px;
                                                        animation: fadeIn 0.8s ease-out;
                                                    }
                                                    @keyframes fadeIn {
                                                        from { opacity: 0; transform: scale(0.95); }
                                                        to { opacity: 1; transform: scale(1); }
                                                    }
                                                    .icon {
                                                        width: 64px; height: 64px;
                                                        background: #f3f4f6; color: #6b7280;
                                                        border-radius: 50%; display: flex;
                                                        align-items: center; justify-content: center;
                                                        margin: 0 auto 20px; font-size: 28px;
                                                    }
                                                    h1 { margin: 0 0 12px; font-weight: 600; font-size: 24px; color: #374151; }
                                                    p { color: #6b7280; line-height: 1.5; margin-bottom: 24px; font-size: 15px; }
                                                    .status {
                                                        font-size: 11px; font-weight: 700;
                                                        text-transform: uppercase; letter-spacing: 1.5px;
                                                        color: #6b7280; background: #f3f4f6;
                                                        padding: 6px 16px; border-radius: 100px;
                                                        display: inline-block; margin-bottom: 10px;
                                                    }
                                                </style>
                                            </head>
                                            <body>
                                                <div class="card">
                                                    <div class="icon">👋</div>
                                                    <div class="status">Logged Out</div>
                                                    <h1>See you later!</h1>
                                                    <p>You have been safely signed out of TurboDev AI. You can now close this tab.</p>
                                                </div>
                                                <script>
                                                    // Auto-close tab after 3 seconds
                                                    setTimeout(() => { window.close(); }, 3000);
                                                </script>
                                            </body>
                                            </html>
                                            `
                                """;
                byte[] responseBytes = response.getBytes(StandardCharsets.UTF_8);
                exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
                exchange.sendResponseHeaders(200, responseBytes.length);
                exchange.getResponseBody().write(response.getBytes());
                exchange.close();

                new Thread(() -> {
                    try {
                        Thread.sleep(15000);
                    } catch (Exception ignored) {
                    }
                    server.stop(0);
                }).start();
            });

            server.start();
        } catch (Exception e) {
            log.info("call back is already in use {}", e.getMessage());
        }
    }

    public void showChat() {
        cardLayout.show(mainPanel, "CHAT");
    }
}
