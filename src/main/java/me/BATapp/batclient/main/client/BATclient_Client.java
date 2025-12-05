package me.BATapp.batclient.main.client;

import com.mojang.blaze3d.systems.RenderSystem;
import me.BATapp.batclient.config.ConfigManager;
import me.BATapp.batclient.config.ConfigurableModule;
import me.BATapp.batclient.config.BATclient_Config;
import me.BATapp.batclient.font.FontRenderers;
import me.BATapp.batclient.gui.BATSettingsScreen;
import me.BATapp.batclient.interfaces.OverlayReloadListener;
import me.BATapp.batclient.main.BATclient_Main;
import me.BATapp.batclient.modules.*;
import me.BATapp.batclient.particle.CustomPhysicParticleFactory;
import me.BATapp.batclient.render.Render3D_Shapes;
import me.BATapp.batclient.render.WatermarkRenderer;
import me.BATapp.batclient.utils.EntityUtils;
import me.BATapp.batclient.utils.HitSound;
import me.BATapp.batclient.utils.OptimizationManager;
import me.BATapp.batclient.utils.UIElementManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.*;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFW;
import me.BATapp.batclient.screen.ChatHandler;

public class BATclient_Client implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        // Initialize fonts lazily on first client tick when window is ready
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (FontRenderers.modules == null) {
                FontRenderers.initialize(client);
            }
            this.doEndClientTick(client);
            OptimizationManager.onClientTick(); // Update optimization systems
        });
        
        // Save UI positions on client stop
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.isInSingleplayer()) {
                // Periodically save (every 20 ticks)
                if (client.world != null && client.world.getTime() % 20 == 0) {
                    UIElementManager.savePositions();
                }
            }
        });
        
        WorldRenderEvents.AFTER_ENTITIES.register(this::doRenderAfterEntities);
        WorldRenderEvents.LAST.register(this::doRenderLast);
//        HudLayerRegistrationCallback.EVENT.register(layeredDrawer -> layeredDrawer.attachLayerBefore(IdentifiedLayer.STATUS_EFFECTS, Identifier.of("batclient", "hud"), this::renderHud));
        HudRenderCallback.EVENT.register(this::renderHud);

        registerClientSideParticles();

        registerOnHit();
        ChatHandler.register();
        Translator.loadCache();
        ConfigManager.loadConfig();
    }

    private void renderHud(DrawContext context, RenderTickCounter tickCounter) {
        TargetHud.render(context, tickCounter);
        WatermarkRenderer.render(context);
        MouseMove.render(context);
        Keystroke.render(context);
        FPS.render(context);
    }

    private void doEndClientTick(MinecraftClient client) {EntityUtils.updateEntities(client);
        OverlayReloadListener.callEvent();

        Trails.onTick();
        JumpCircles.onTick();
        BreakingAnimation.onTick();
        TargetHud.onTick();
        RPC.onTick();
        Translator.onTick();

        HitParticle.onTick();
        TotemPopParticles.onTick();
        JumpParticles.onTick();
        AmbientParticle.onTick();
        
        // update placing animations
        me.BATapp.batclient.animation.PlacingAnimationManager.tick();

        Zoom.onTick();
        Freelook.onTick();
        Freecam.onTick();
        PerformanceOptimizer.onTick();

        // FullBright applies clientside Night Vision when enabled
        FullBright.onClientTick();
        // Saturation module keeps hunger saturated clientside
        me.BATapp.batclient.modules.Saturation.onClientTick();

//        KillEffect.onTick();

        long handle = client.getWindow().getHandle();
        Screen parent = null;
        Screen currentScreen = client.currentScreen;
        if (currentScreen instanceof ChatScreen) return;

        if (InputUtil.isKeyPressed(handle, GLFW.GLFW_KEY_RIGHT_CONTROL) && InputUtil.isKeyPressed(handle, GLFW.GLFW_KEY_RIGHT_ALT)) {
            client.setScreen(new BATSettingsScreen());
        }

        // Otevře nastavení modulů stiskem pravého Shiftu
        if (InputUtil.isKeyPressed(handle, GLFW.GLFW_KEY_RIGHT_SHIFT)) {
            client.setScreen(new BATSettingsScreen());
        }
        if (client.options.sprintKey.isPressed()){
//            client.setScreen(new TestRenderer());
//            client.setScreen(new BATSettingsScreen());
        }
    }

    private void doRenderAfterEntities(WorldRenderContext context) {
        // Render particles here to ensure they appear before HUD overlays
        renderParticles(context);
    }

    private void doRenderLast(WorldRenderContext context) {
        TargetRender.renderTarget(context);
        HitBubbles.render(context);
        Halo.render(context);
        Trails.renderTrail(context);
        JumpCircles.renderCircles(context);
        BreakingAnimation.render(context);
        me.BATapp.batclient.animation.PlacingAnimationManager.render(context);
        Trajectories.render(context);

        // Render particles in LAST event as fallback/additional render pass
        if (context != null) {
            renderParticles(context);
        }
        Render3D_Shapes.render(context);
    }

    private void registerOnHit() {
        HitBubbles.registerOnHit();
        HitSound.registerOnHit();
        HitParticle.registerOnHit();
    }

    private void registerClientSideParticles() {
        ParticleFactoryRegistry factoryRegistry = ParticleFactoryRegistry.getInstance();
        factoryRegistry.register(BATclient_Main.STAR, CustomPhysicParticleFactory::new);
    }

    private void renderParticles(WorldRenderContext context) {
        if (context == null) return;
        
        JumpParticles.render(context);
        HitParticle.render(context);
        TotemPopParticles.render(context);
        AmbientParticle.render(context);
    }

}