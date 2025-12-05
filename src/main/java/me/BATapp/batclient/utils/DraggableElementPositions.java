package me.BATapp.batclient.utils;

/**
 * Utility class for managing positions of draggable HUD elements
 */
public class DraggableElementPositions {
    
    // Scoreboard positions
    public static float scoreboardX = 10;
    public static float scoreboardY = 10;
    public static boolean scoreboardDragging = false;
    public static int scoreboardDragOffsetX = 0;
    public static int scoreboardDragOffsetY = 0;
    
    // BossBar positions
    public static float bossbarX = 0;
    public static float bossbarY = 10;
    public static boolean bossbarDragging = false;
    public static int bossbarDragOffsetX = 0;
    public static int bossbarDragOffsetY = 0;
    
    // Scoreboard methods
    public static void setScoreboardPosition(float x, float y) {
        scoreboardX = Math.max(0, Math.min(x, 1920 - 150));
        scoreboardY = Math.max(0, Math.min(y, 1080 - 100));
    }

    public static float getScoreboardX() {
        return scoreboardX;
    }

    public static float getScoreboardY() {
        return scoreboardY;
    }

    public static void setScoreboardDragging(boolean dragging, int offsetX, int offsetY) {
        scoreboardDragging = dragging;
        scoreboardDragOffsetX = offsetX;
        scoreboardDragOffsetY = offsetY;
    }

    public static boolean isScoreboardDragging() {
        return scoreboardDragging;
    }

    public static int getScoreboardDragOffsetX() {
        return scoreboardDragOffsetX;
    }

    public static int getScoreboardDragOffsetY() {
        return scoreboardDragOffsetY;
    }

    // BossBar methods
    public static void setBossbarPosition(float x, float y) {
        bossbarX = Math.max(0, Math.min(x, 1920 - 182));
        bossbarY = Math.max(0, Math.min(y, 1080 - 10));
    }

    public static float getBossbarX() {
        return bossbarX;
    }

    public static float getBossbarY() {
        return bossbarY;
    }

    public static void setBossbarDragging(boolean dragging, int offsetX, int offsetY) {
        bossbarDragging = dragging;
        bossbarDragOffsetX = offsetX;
        bossbarDragOffsetY = offsetY;
    }

    public static boolean isBossbarDragging() {
        return bossbarDragging;
    }

    public static int getBossbarDragOffsetX() {
        return bossbarDragOffsetX;
    }

    public static int getBossbarDragOffsetY() {
        return bossbarDragOffsetY;
    }
}
