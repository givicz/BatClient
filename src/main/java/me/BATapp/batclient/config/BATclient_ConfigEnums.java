package me.BATapp.batclient.config;

/**
 * Enumeration types for BATclient configuration
 * Used to strongly type CONFIG values instead of using String
 */
public class BATclient_ConfigEnums {

    // Jump Circles Style
    public enum JumpCirclesStyle {
        CIRCLE, CIRCLE_BOLD, HEXAGON, PORTAL, SOUP
    }

    // Jump Particles Physic
    public enum JumpParticlesPhysic {
        GRAVITY, BOUNCE
    }

    // Jump Particles Disappear
    public enum JumpParticlesDisappear {
        ALPHA, SCALE
    }

    // Halo Soul Style
    public enum HaloSoulStyle {
        SMOKE, PLASMA
    }

    // Hit Bubbles Style
    public enum HitBubblesStyle {
        CIRCLE, CIRCLE_BOLD, HEXAGON, PORTAL, PORTAL_2
    }

    // Hit Particles Physic
    public enum HitParticlesPhysic {
        GRAVITY, BOUNCE
    }

    // Hit Particles Disappear
    public enum HitParticlesDisappear {
        ALPHA, SCALE
    }

    // Hit Particles Text Mode
    public enum HitParticlesTextMode {
        DISABLED, ALL_ENTITIES, ONLY_SELF_DAMAGE
    }

    // Target Hud Style
    public enum TargetHudStyle {
        MINI, TINY, ARES, ALT_1, NORMAL
    }

    // Target Render Style
    public enum TargetRenderStyle {
        LEGACY, SOUL, SPIRAL, TOPKA
    }

    // Target Render Legacy Texture
    public enum TargetRenderLegacyTexture {
        LEGACY, MARKER, BO, SIMPLE, SCIFI, JEKA, AMOGUS, SKULL, VEGAS
    }

    // Target Render Soul Texture
    public enum TargetRenderSoulTexture {
        FIREFLY, ALT
    }

    // Target Render Soul Style
    public enum TargetRenderSoulStyle {
        SMOKE, PLASMA
    }

    // Totem Pop Particles Disappear
    public enum TotemPopParticlesDisappear {
        ALPHA, SCALE
    }

    // Trails Style
    public enum TrailsStyle {
        FADED, FADED_INVERT, SOLID
    }

    // RPC State
    public enum RPCState {
        NAME, IP, CUSTOM
    }

    // Palette Style
    public enum PaletteStyle {
        SOLO, DUO, TRIO, QUARTET
    }

    // MC Tiers Game Modes
    public enum MCTiersGameMode {
        LTMs, VANILLA, UHC, POT, NETHER_OP, SMP, SWORD, AXE, MACE
    }
}
