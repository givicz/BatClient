package me.BATapp.batclient.modules;

import me.BATapp.batclient.config.ConfigurableModule;
import net.minecraft.text.Text;

public class SwingHand extends ConfigurableModule {
    public static float xPos = 0.7F;
    public static float yPos = -0.4F;
    public static float zPos = -0.85F;
    public static float scale = 0.75F;
    public static int rotX = 0;
    public static int rotY = -13;
    public static int rotZ = 8;
    public static int xSwingRot = -55;
    public static int ySwingRot = 0;
    public static int zSwingRot = 90;
    public static int speed = 100;

    private static boolean isConfigScreen() {
        return mc.currentScreen == null ? false : mc.currentScreen.getTitle().equals(Text.of("Swing Hand Screen"));
    }

    public static float getxPos() {
        return isConfigScreen() ? xPos : CONFIG.swingHand_xPos;
    }

    public static float getyPos() {
        return isConfigScreen() ? yPos : CONFIG.swingHand_yPos;
    }

    public static float getzPos() {
        return isConfigScreen() ? zPos : CONFIG.swingHand_zPos;
    }

    public static float getScale() {
        return isConfigScreen() ? scale : CONFIG.swingHand_scale;
    }

    public static int getxSwingRot() {
        return isConfigScreen() ? xSwingRot : (int)CONFIG.swingHand_xSwingRot;
    }

    public static int getySwingRot() {
        return isConfigScreen() ? ySwingRot : (int)CONFIG.swingHand_ySwingRot;
    }

    public static int getzSwingRot() {
        return isConfigScreen() ? zSwingRot : (int)CONFIG.swingHand_zSwingRot;
    }

    public static int getRotX() {
        return isConfigScreen() ? rotX : (int)CONFIG.swingHand_rotX;
    }

    public static int getRotY() {
        return isConfigScreen() ? rotY : (int)CONFIG.swingHand_rotY;
    }

    public static int getRotZ() {
        return isConfigScreen() ? rotZ : (int)CONFIG.swingHand_rotZ;
    }

    public static int getSpeed() {
        return isConfigScreen() ? speed : (int)CONFIG.swingHand_speed;
    }

    public static enum Config {
        CONFIG_HANDS
    }
}