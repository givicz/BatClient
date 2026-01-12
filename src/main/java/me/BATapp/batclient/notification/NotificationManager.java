package me.BATapp.batclient.notification;

import me.BATapp.batclient.font.FontRenderers;
import me.BATapp.batclient.render.Render2D;
import me.BATapp.batclient.utils.MathUtility;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;

import java.awt.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class NotificationManager {
    private static final CopyOnWriteArrayList<Notification> notifications = new CopyOnWriteArrayList<>();

    public static void show(NotificationType type, String title, String message, float seconds) {
        notifications.add(new Notification(type, title, message, seconds));
    }

    public static void render(DrawContext context) {
        if (notifications.isEmpty()) return;

        int screenWidth = MinecraftClient.getInstance().getWindow().getScaledWidth();
        int screenHeight = MinecraftClient.getInstance().getWindow().getScaledHeight();

        float y = screenHeight - 50; // Start offset from bottom

        // Iterate backwards to render newer ones at the bottom or top depending on preference.
        // Let's stack upwards from bottom right.
        for (Notification notification : notifications) {
            long timeElapsed = notification.getTimeElapsed();
            float animationDuration = 500; // ms
            float alpha = 1.0f;
            float slideX = 0;

            if (timeElapsed < animationDuration) {
                // Fade in / Slide in
                float progress = timeElapsed / animationDuration;
                progress = MathUtility.clamp(progress, 0, 1);
                // Ease out back
                float t = progress - 1;
                float ease = t * t * ((1.70158f + 1) * t + 1.70158f) + 1;
                
                slideX = (1 - ease) * 200; // Slide from right
                alpha = progress;
            } else if (notification.isExpired()) {
                 // Remove is dangerous inside iterator unless CopyOnWriteArrayList
                notifications.remove(notification);
                continue;
            } else if (notification.isExpired() || notification.getTimeElapsed() > notification.getDuration() - animationDuration) {
                 // Fade out
                long remaining = notification.getDuration() - notification.getTimeElapsed();
                if (remaining < 0) remaining = 0;
                float progress = remaining / animationDuration;
                alpha = progress;
            }
            
            float width = 180;
            float height = 35;
            float x = screenWidth - width - 10 + slideX;

            renderNotification(context, notification, x, y, width, height, alpha);
            y -= (height + 5);
        }
    }

    private static void renderNotification(DrawContext context, Notification notification, float x, float y, float width, float height, float alpha) {
        MatrixStack stack = context.getMatrices();
        Color bgColor = new Color(15, 15, 15, (int)(240 * alpha));
        Color textColor = new Color(255, 255, 255, (int)(255 * alpha));
        Color subTextColor = new Color(180, 180, 180, (int)(255 * alpha));
        Color accentColor = notification.getType().getColor();
        // Adjust alpha for accent
        // Color accentWithAlpha = new Color(accentColor.getRed(), accentColor.getGreen(), accentColor.getBlue(), (int)(255 * alpha));

        // Background
        Render2D.drawRound(stack, x, y, width, height, 5, bgColor);

        // Icon bg
        // Render2D.drawRound(stack, x + 5, y + 5, 25, 25, 4, new Color(30,30,30, (int)(255*alpha)));
        
        // Simple Icon (placeholder circle)
        // Render2D.drawCircle(stack, x + 16, y + 17, 0, 10, accentColor.getRGB());
        Render2D.drawRound(stack, x, y, 4, height, 5, accentColor); // Left Strip with rounded corners on left? 
        // Actually drawerRound draws all corners.
        // Let's create a "Left Strip" indicator
        Render2D.renderRoundedQuadInternal(stack.peek().getPositionMatrix(), accentColor.getRed()/255f, accentColor.getGreen()/255f, accentColor.getBlue()/255f, alpha, x, y, x + 4, y + height, 5, 5); // Assuming this works like that

        if (FontRenderers.inter_bold != null) {
            FontRenderers.inter_bold.drawString(stack, notification.getTitle(), x + 15, y + 6, textColor.getRGB());
            FontRenderers.inter_medium.drawString(stack, notification.getMessage(), x + 15, y + 18, subTextColor.getRGB());
        }

        // Progress bar at bottom
        float progress = notification.getProgress();
        float barWidth = (width - 10) * (1 - progress);
        if (barWidth > 0 && barWidth < width) {
            Render2D.drawRound(stack, x + 5, y + height - 4, barWidth, 2, 1, accentColor);
        }
    }
}
