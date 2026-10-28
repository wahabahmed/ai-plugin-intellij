package com.turbo.dev.plugin.utils;

import com.intellij.openapi.util.IconLoader;
import com.intellij.util.IconUtil;

import javax.swing.*;
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.*;

public class UiUtils {
    public static Color USER_COLOR = new Color(0, 120, 215);
    public static Color SYSTEM_COLOR = new Color(227, 242, 253);

    public static JButton getPrimaryButton(String label) {
        JButton button = new JButton(label);
        Color customBlue = new Color(0, 120, 215);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setPreferredSize(new Dimension(100, 50));
        button.setSize(new Dimension(100, 50));

        button.setUI(new BasicButtonUI() {

            @Override
            public void update(Graphics g, JComponent c) {
                paintBackground(g, c);
                super.paint(g, c);
            }

            private void paintBackground(Graphics g, JComponent c) {
                Graphics2D g2 = (Graphics2D) g.create();

                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(customBlue);
                g2.fillRoundRect(0, 0, c.getWidth(), c.getHeight(), 12, 12);

                g2.dispose();
            }
        });

        //button.setForeground(Color.WHITE); // White text for contrast
        //button.setBackground(customBlue);

        //button.setContentAreaFilled(false); // Removes the native grey paint
        //button.setOpaque(true);             // Tells Swing to use your background color
        //button.setBorderPainted(false);     // Removes the native 3D border
        //button.setFocusPainted(false);

        /*button.setForeground(Color.WHITE);

        button.putClientProperty("JButton.buttonType", "default");
        button.putClientProperty("JButton.background", new Color(0, 120, 215));
        button.putClientProperty("JButton.paintButtonBackground", true);

        button.setFocusPainted(false);*/

        return button;
    }

    public static JButton getSecondaryButton(String label) {
        return new JButton(label);
    }

    public static Icon getScaledIcon(Class<?> clazz) {
        Icon icon = IconLoader.getIcon("/icons/ai-new.png", clazz);
        return IconUtil.scale(icon, null, 0.09f);
    }
}
