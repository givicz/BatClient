package me.BATapp.batclient.mixin.inject;

import me.BATapp.batclient.modules.PerformanceOptimizer;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class PacketTrackingMixin {
    
    @Inject(method = "tick", at = @At("HEAD"))
    private void trackTick(CallbackInfo ci) {
        // Track periodic activity (used for AFK detection)
        PerformanceOptimizer.onPacketReceived();
    }
}
