package me.BATapp.batclient.modules;

import me.BATapp.batclient.SoupModule;
import me.BATapp.batclient.settings.impl.BooleanSetting;
import me.BATapp.batclient.settings.impl.ColorSetting;

public class ChinaHat extends SoupModule {
    public static final BooleanSetting enabled = new BooleanSetting("Enabled", "Enable China Hat", false);
    public static final ColorSetting color = new ColorSetting("Color", "China Hat color", 0xFF00d4ff);

    public ChinaHat() {
        super("China Hat", Category.PARTICLES);
    }
}
