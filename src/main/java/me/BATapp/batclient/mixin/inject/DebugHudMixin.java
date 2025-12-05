package me.BATapp.batclient.mixin.inject;

import me.BATapp.batclient.modules.PerformanceOptimizer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.DebugHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DebugHud.class)
public class DebugHudMixin {

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void onRender(DrawContext context, CallbackInfo ci) {  // ✅ Odebrán RenderTickCounter parametr
        if (!PerformanceOptimizer.shouldMinimizeF3Debug()) return;

        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.world == null) return;

        // Render only essential info: FPS, Coords, Direction
        int y = 2;
        int color = 0xE0E0E0;

        // FPS
        int fps = client.getCurrentFps();
        context.drawTextWithShadow(client.textRenderer, "FPS: " + fps, 2, y, color);
        y += 10;

        // Coordinates
        String coords = String.format("XYZ: %.2f / %.2f / %.2f",
                client.player.getX(),
                client.player.getY(),
                client.player.getZ());
        context.drawTextWithShadow(client.textRenderer, coords, 2, y, color);
        y += 10;

        // Direction
        float yaw = client.player.getYaw();
        String direction = getDirection(yaw);
        context.drawTextWithShadow(client.textRenderer, "Direction: " + direction + " (" + String.format("%.1f", yaw) + ")", 2, y, color);

        ci.cancel(); // Cancel original debug render
    }

    private String getDirection(float yaw) {
        yaw = (yaw % 360 + 360) % 360; // Normalize to 0-360
        if (yaw >= 337.5 || yaw < 22.5) return "South";
        if (yaw >= 22.5 && yaw < 67.5) return "South-West";
        if (yaw >= 67.5 && yaw < 112.5) return "West";
        if (yaw >= 112.5 && yaw < 157.5) return "North-West";
        if (yaw >= 157.5 && yaw < 202.5) return "North";
        if (yaw >= 202.5 && yaw < 247.5) return "North-East";
        if (yaw >= 247.5 && yaw < 292.5) return "East";
        return "South-East";
    }
}