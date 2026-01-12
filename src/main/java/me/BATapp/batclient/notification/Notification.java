package me.BATapp.batclient.notification;

public class Notification {
    private final NotificationType type;
    private final String title;
    private final String message;
    private final long duration;
    private final long startTime;
    
    // Animation states
    public float xOffset = 0;
    public float yOffset = 0;

    public Notification(NotificationType type, String title, String message, float seconds) {
        this.type = type;
        this.title = title;
        this.message = message;
        this.duration = (long) (seconds * 1000);
        this.startTime = System.currentTimeMillis();
    }

    public NotificationType getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }

    public long getStartTime() {
        return startTime;
    }

    public long getTimeElapsed() {
        return System.currentTimeMillis() - startTime;
    }

    public boolean isExpired() {
        return getTimeElapsed() > duration;
    }
    
    public float getProgress() {
        return Math.min(1.0f, (float) getTimeElapsed() / duration);
    }
}
