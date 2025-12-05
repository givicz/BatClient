package me.BATapp.batclient.modules;

import me.BATapp.batclient.SoupModule;
import me.BATapp.batclient.settings.impl.BooleanSetting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;

import static me.BATapp.batclient.modules.FullBright.enabled;

public class FullBright extends SoupModule {
    public static final BooleanSetting enabled = new BooleanSetting("FullBright", "Force full bright (disable lighting)", false);

    public FullBright() {
        super("FullBright", Category.WORLD);
    }

    public static void onClientTick() {
        if (!enabled.getValue()) return;
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null) return;
        try {
            // Apply short-duration night vision each tick so it is client-side only and persistent
            StatusEffectInstance nv = new StatusEffectInstance(StatusEffects.NIGHT_VISION, 40, 0, false, false, false);
            mc.player.addStatusEffect(nv);
        } catch (Throwable ignored) {}
    }
}
