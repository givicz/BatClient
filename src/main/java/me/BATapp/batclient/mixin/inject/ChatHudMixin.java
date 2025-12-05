package me.BATapp.batclient.mixin.inject;

import org.spongepowered.asm.mixin.Mixin;
import net.minecraft.client.gui.hud.ChatHud;

@Mixin(ChatHud.class)
public class ChatHudMixin {
    // Chat message clicking is handled natively by Minecraft 1.21.4
    // No custom implementation needed
}
