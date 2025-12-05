package me.BATapp.batclient.modules;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import me.BATapp.batclient.config.ConfigurableModule;
import me.BATapp.batclient.config.BATclient_ConfigEnums;
import me.BATapp.batclient.render.Render3D;
import me.BATapp.batclient.utils.EntityUtils;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.entity.Entity;

public class Halo extends ConfigurableModule {

    public static void render(WorldRenderContext context) {
        float tickDelta = context.tickCounter().getTickDelta(true);

        if (mc.world == null || mc.player == null || !CONFIG.haloEnabled) return;

        boolean isFirstPerson = mc.options.getPerspective().isFirstPerson();

        for (Entity entity : mc.world.getEntities()) {
            if (EntityUtils.isFriend(entity) || entity.equals(mc.player)) {
                if (entity == mc.player && isFirstPerson) continue;
                if (!mc.player.canSee(entity) || entity.isInvisible()) return;

                Render3D.renderSoulPair(tickDelta, entity);
            }
        }
    }

    public enum SoulStyle {
        SMOKE, PLASMA;

        public static void setupBlendFunc() {
            switch (CONFIG.haloSoulRenderSoulStyle) {
                case SMOKE -> RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_COLOR);
                case PLASMA -> RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE);
            }
        }
    }
}
