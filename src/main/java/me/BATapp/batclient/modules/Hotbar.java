package me.BATapp.batclient.modules;

import me.BATapp.batclient.SoupModule;
import me.BATapp.batclient.settings.impl.BooleanSetting;
import me.BATapp.batclient.settings.impl.ColorSetting;

public class Hotbar extends SoupModule {
    public static final BooleanSetting enabled = new BooleanSetting("Enabled", "Enable custom hotbar color", false);
    public static final ColorSetting color = new ColorSetting("Color", "Hotbar color", 0xFF00d4ff);

    public Hotbar() {
        super("Hotbar", Category.HUD);
    }
}
