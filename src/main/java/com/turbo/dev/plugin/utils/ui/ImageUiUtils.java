package com.turbo.dev.plugin.utils.ui;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.fileChooser.FileChooserFactory;
import com.intellij.openapi.fileChooser.FileSaverDescriptor;
import com.intellij.openapi.fileChooser.FileSaverDialog;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileWrapper;
import com.intellij.ui.jcef.JBCefBrowser;
import com.intellij.ui.jcef.JBCefBrowserBase;
import com.intellij.ui.jcef.JBCefJSQuery;
import com.turbo.dev.plugin.ui.RoundedBubble;
import com.turbo.dev.plugin.utils.UiUtils;
import org.cef.browser.CefBrowser;
import org.cef.browser.CefFrame;
import org.cef.handler.CefLoadHandlerAdapter;

import javax.swing.*;
import java.awt.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import static com.turbo.dev.plugin.utils.UiUtils.SYSTEM_COLOR;

public class ImageUiUtils {
    public static void prepareDiagram(JPanel chatArea, JScrollPane scrollPane, String diagramCode) {
        // 1. OUTER WRAPPER (To align left like a bot message)
        JPanel alignPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        alignPanel.setOpaque(false);

        // 2. THE BUBBLE (Reuse your RoundedBubble class)
        // Color (230, 230, 230) is what you used for bot messages in addMessage
        RoundedBubble bubble = new RoundedBubble(SYSTEM_COLOR);
        bubble.setLayout(new BoxLayout(bubble, BoxLayout.Y_AXIS));

        String html = """
                                <!DOCTYPE html>
                                       <html>
                                       <head>
                
                                       <script src="https://cdn.jsdelivr.net/npm/mermaid@11/dist/mermaid.min.js"></script>
                
                                       <style>
                                       body {
                                           font-family: sans-serif;
                                           padding: 10px;
                                       }
                
                                       svg {
                                           width: 100%;
                                           height: auto;
                                       }
                                       </style>
                
                                       </head>
                
                                       <body>                      
                                       <div id="chat"></div>
                
                                       <script>
                
                                       mermaid.initialize({
                                           startOnLoad: false,
                                           securityLevel: 'loose'
                                       });
                
                                       window.renderDiagram = async function(code) {
                                           const chat = document.getElementById("chat");                
                                           const id = "m_" + Date.now();                
                                           const div = document.createElement("div");
                                           div.id=id;                
                                           chat.appendChild(div);              
                                           try {
                                               const result = await mermaid.render(id + "_svg", code);                
                                               div.innerHTML = `
                                                                                                       <div class="diagram">
                                                                                                           ${result.svg}
                                                                                                       </div>
                                                                                                   `;               
                                           } catch (e) {
                                               div.innerHTML = "<pre style='color:red'>" + e + "</pre>";
                                           }
                                       };
                
                
                                       </script>
                
                                       </body>
                                       </html>
                """;

        JBCefBrowser browser = new JBCefBrowser();
        browser.loadHTML(html);

        Component view = browser.getComponent();
        view.setPreferredSize(new Dimension(400, 300));
        view.setMinimumSize(new Dimension(200, 400));
        view.setMaximumSize(new Dimension(400, 400));

        browser.getJBCefClient().addLoadHandler(
                new CefLoadHandlerAdapter() {

                    @Override
                    public void onLoadEnd(CefBrowser cefBrowser,
                                          CefFrame frame,
                                          int httpStatusCode) {

                        if (!frame.isMain()) return;

                        // NOW safe to call JS
                        cefBrowser.executeJavaScript(
                                "window.renderDiagram(`" + diagramCode + "`);",
                                cefBrowser.getURL(),
                                0
                        );
                    }
                },
                browser.getCefBrowser()
        );


        JButton saveBtn = UiUtils.getPrimaryButton("<html><body>&#x2B07;</body></html>");
        saveBtn.setPreferredSize(new Dimension(50, 30));
        saveBtn.setMinimumSize(new Dimension(50, 30));
        saveBtn.setMaximumSize(new Dimension(50, 30));
        saveBtn.setAlignmentX(Component.LEFT_ALIGNMENT);

        JBCefJSQuery svgQuery = JBCefJSQuery.create((JBCefBrowserBase) browser);
        svgQuery.addHandler(svgText -> {

            ApplicationManager.getApplication().invokeLater(() -> {

                try {
                    FileSaverDescriptor descriptor =
                            new FileSaverDescriptor(
                                    "Save SVG",
                                    "Export diagram as SVG",
                                    "svg"
                            );

                    FileSaverDialog dialog =
                            FileChooserFactory.getInstance()
                                    .createSaveFileDialog(
                                            descriptor,
                                            (Project) null
                                    );

                    VirtualFileWrapper wrapper =
                            dialog.save(
                                    (VirtualFile) null,
                                    "diagram_" + System.currentTimeMillis() + ".svg"
                            );

                    if (wrapper == null) return;

                    Files.writeString(
                            wrapper.getFile().toPath(),
                            svgText,
                            StandardCharsets.UTF_8
                    );

                    Messages.showInfoMessage(
                            "SVG exported successfully",
                            "Success"
                    );

                } catch (Exception ex) {

                    Messages.showErrorDialog(
                            ex.getMessage(),
                            "Export Failed"
                    );
                }
            });

            return null;
        });

        saveBtn.addActionListener(e -> {
            browser.getCefBrowser().executeJavaScript(
                    """
                            (function() {
                            
                                const svg = document.querySelector("svg");
                            
                                if (!svg) {
                                    console.error("No SVG found in DOM");
                                    return;
                                }
                            
                                // serialize REAL SVG DOM (vector output)
                                const svgText =
                                    '<?xml version="1.0" encoding="UTF-8"?>\\n' +
                                    new XMLSerializer().serializeToString(svg);
                            
                                %s;
                            
                            })();
                            """.formatted(svgQuery.inject("svgText")),
                    browser.getCefBrowser().getURL(),
                    0
            );
        });


        bubble.setLayout(new BorderLayout());
        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        topBar.setBackground(Color.WHITE);
        topBar.setOpaque(true);
        topBar.add(saveBtn);

        // add components
        bubble.add(topBar, BorderLayout.NORTH);

        //bubble.add(saveBtn, BorderLayout.WEST);
        bubble.add(view, BorderLayout.CENTER);

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
}
