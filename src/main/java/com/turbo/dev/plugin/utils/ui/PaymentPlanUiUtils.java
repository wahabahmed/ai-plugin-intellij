package com.turbo.dev.plugin.utils.ui;

import com.turbo.dev.plugin.dto.subscription.SubscriptionPayment;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/*public class PaymentPlanUiUtils{

    public static JPanel createPlans(List<SubscriptionPayment> plans) {

        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("💳 Payment Plans");
        title.setFont(new Font("Arial", Font.BOLD, 14));
        container.add(title);

        for (SubscriptionPayment plan : plans) {
            container.add(createCard(plan));
            container.add(Box.createVerticalStrut(10));
        }

        return container;
    }

    private static JPanel createCard(SubscriptionPayment plan) {

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createLineBorder(Color.GRAY));

        String price = String.format("$%.2f", plan.getPrice() / 100.0);
        String tokens = plan.getTokens() == -1 ? "Unlimited" : String.valueOf(plan.getTokens());

        JLabel name = new JLabel(plan.getPlanType().name());
        JLabel priceLabel = new JLabel(price);
        JLabel tokenLabel = new JLabel(tokens + " tokens");

        JButton btn = new JButton("Subscribe");

        btn.addActionListener(e -> {
            try {
                Desktop.getDesktop().browse(new java.net.URI(plan.getPaymentUrl()));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        card.add(name);
        card.add(priceLabel);
        card.add(tokenLabel);
        card.add(btn);

        return card;
    }
}*/

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.URI;
import java.util.List;

import static com.turbo.dev.plugin.utils.UiUtils.SYSTEM_COLOR;

public class PaymentPlanUiUtils {

    public static JPanel createPlans(List<SubscriptionPayment> plans) {

        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setBackground(SYSTEM_COLOR);

        JLabel title = new JLabel("💳 Payment Plans");
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        title.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        container.add(title);
        for (SubscriptionPayment plan : plans) {
            container.add(createBubble(plan));
            container.add(Box.createVerticalStrut(6));
        }

        return container;
    }

    private static JPanel createBubble(SubscriptionPayment plan) {

        JPanel bubble = new RoundedPanel(18, new Color(255, 255, 255));
        bubble.setLayout(new BorderLayout());
        bubble.setBorder(BorderFactory.createEmptyBorder(12, 14, 12, 14));
        bubble.setMaximumSize(new Dimension(Integer.MAX_VALUE, 140));

        // LEFT CONTENT (text)
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);

        JLabel name = new JLabel(plan.getPlanType().name());
        name.setFont(new Font("Segoe UI", Font.BOLD, 14));

        String price = String.format("$%.2f", plan.getPrice() / 100.0);
        JLabel priceLabel = new JLabel(price);
        priceLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        priceLabel.setForeground(new Color(80, 80, 80));

        String tokens = plan.getTokens() == -1 ? "Unlimited tokens" : plan.getTokens() + " tokens";
        JLabel tokenLabel = new JLabel(tokens);
        tokenLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tokenLabel.setForeground(new Color(120, 120, 120));

        textPanel.add(name);
        textPanel.add(Box.createVerticalStrut(4));
        textPanel.add(priceLabel);
        textPanel.add(tokenLabel);

        // RIGHT BUTTON
        JButton btn = new JButton("Subscribe →");
        btn.setFocusPainted(false);
        btn.setBackground(new Color(70, 130, 255));
        btn.setForeground(Color.BLACK);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addActionListener(e -> {
            try {
                Desktop.getDesktop().browse(new URI(plan.getPaymentUrl()));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        right.setOpaque(false);
        right.add(btn);

        bubble.add(textPanel, BorderLayout.CENTER);
        bubble.add(right, BorderLayout.EAST);

        return bubble;
    }

    // Rounded panel helper
    static class RoundedPanel extends JPanel {
        private final int radius;
        private final Color backgroundColor;

        public RoundedPanel(int radius, Color bg) {
            this.radius = radius;
            this.backgroundColor = bg;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            // soft shadow
            g2.setColor(new Color(0, 0, 0, 10));
            g2.fillRoundRect(3, 3, getWidth() - 6, getHeight() - 6, radius, radius);

            // main bubble
            g2.setColor(backgroundColor);
            g2.fillRoundRect(0, 0, getWidth() - 6, getHeight() - 6, radius, radius);

            g2.dispose();
            super.paintComponent(g);
        }
    }
}
