package me.BATapp.batclient.mixin.inject;

import me.BATapp.batclient.modules.Freecam;
import me.BATapp.batclient.modules.Freelook;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;  // ✅ Změněno z BlockRenderView
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Camera.class)
public abstract class CameraMixin {

    @Shadow private float yaw;
    @Shadow private float pitch;
    @Shadow private Vec3d pos;

    @Inject(method = "update", at = @At("HEAD"), cancellable = true)
    private void onUpdate(
            BlockView area,           // ✅ Změněno z BlockRenderView
            Entity focusedEntity,
            boolean thirdPerson,
            boolean inverseView,
            float tickDelta,
            CallbackInfo ci
    ) {
        if (Freecam.isActive()) {
            this.pos = Freecam.getCameraPos();
            this.yaw = Freecam.getCameraYaw();
            this.pitch = Freecam.getCameraPitch();
            ci.cancel();
        } else if (Freelook.isActive()) {
            this.yaw = Freelook.getCameraYaw();
            this.pitch = Freelook.getCameraPitch();
        }
    }
}