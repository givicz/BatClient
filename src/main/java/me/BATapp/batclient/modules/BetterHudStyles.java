package me.BATapp.batclient.modules;

import me.BATapp.batclient.SoupModule;
import me.BATapp.batclient.screen.ConfigHudPositionsScreen;
import me.BATapp.batclient.settings.impl.BooleanSetting;
import me.BATapp.batclient.settings.impl.ButtonSetting;
import me.BATapp.batclient.settings.impl.EnumSetting;

public class BetterHudStyles extends SoupModule {
    @SuppressWarnings("unused")
    public static ButtonSetting openHudCfg = new ButtonSetting("Config HUD", "das", () -> mc.setScreen(new ConfigHudPositionsScreen()));

    public static BooleanSetting betterHotbar = new BooleanSetting("Better Hotbar", "dasdas", false);
    public static BooleanSetting betterHotbarArmor = new BooleanSetting("Show Armor", "dasdas", false);
    public static BooleanSetting betterHotbarSmoothScroll = new BooleanSetting("Smooth Scroll", "dasdas", false);
    public static final EnumSetting<HotbarStyle> betterHotbarStyle = new EnumSetting<>("Style", "descripdstion", HotbarStyle.SIMPLE, HotbarStyle.class);
    public static final me.BATapp.batclient.settings.impl.BooleanSetting hotbarColorEnabled = new me.BATapp.batclient.settings.impl.BooleanSetting("Custom Hotbar Color", "Enable custom hotbar color", false);
    public static final me.BATapp.batclient.settings.impl.ColorSetting hotbarColor = new me.BATapp.batclient.settings.impl.ColorSetting("Hotbar Color", "Hotbar color", 0xFF00d4ff);

    public BetterHudStyles() {
        super("Better HUD", Category.HUD);
    }

    public enum HotbarStyle {
        GLOW,
        SIMPLE
    }
}
