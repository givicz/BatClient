package me.BATapp.batclient.notification;

import java.awt.Color;

public enum NotificationType {
    INFO(new Color(255, 255, 255), "info"),
    SUCCESS(new Color(100, 255, 100), "success"),
    WARNING(new Color(255, 200, 50), "warning"),
    ERROR(new Color(255, 100, 100), "error");

    private final Color color;
    private final String iconName;

    NotificationType(Color color, String iconName) {
        this.color = color;
        this.iconName = iconName;
    }

    public Color getColor() {
        return color;
    }

    public String getIconName() {
        return iconName;
    }
}
