package me.BATapp.batclient.config;

import me.BATapp.batclient.modules.*;
import me.BATapp.batclient.sounds.CustomSounds;
import me.BATapp.batclient.utils.MC_Tiers;
import me.BATapp.batclient.utils.Palette;
import me.BATapp.batclient.utils.Weather;
import java.util.ArrayList;
import java.util.List;

/**
 * Configuration class - all settings managed in-game via BATSettingsScreen
 * Fields are public static for direct access throughout the codebase
 * No external configuration framework (ModMenu/AutoConfig/Cloth Config) is used
 */
public class BATclient_Config {
    // Ambient Particles
    public static boolean ambientParticlesRandomColor = false;

    // Blur/Shadow
    public static boolean blurShadowEnabled = true;

    // China Hat
    public static boolean chinaHatEnabled = true;
    public static float chinaHatBaseRadius = 0.3f;
    public static float chinaHatTipHeight = 0.5f;
    public static float chinaHatYOffset = 0.2f;
    public static float chinaHatAlpha = 50;
    public static boolean chinaHatRenderHalf = false;

    // Color Settings
    public static int c1 = 0xFFFFFFFF;
    public static int c2 = 0xFFFFFFFF;
    public static int c3 = 0xFFFFFFFF;
    public static int c4 = 0xFFFFFFFF;
    public static int backColor = 0xFF000000;
    public static float backAlpha = 100;
    public static int textColor = 0xFFFFFFFF;
    public static BATclient_ConfigEnums.PaletteStyle paletteStyle = BATclient_ConfigEnums.PaletteStyle.SOLO;

    // Custom Capes
    public static boolean customCapesEnabled = false;
    public static String customCapesLink = null;

    // Friends Highlight
    public static boolean friendsHighlight = true;
    public static boolean friendsHighlightOnlyArmor = false;
    public static int friendCustomColor = 0x00FF00;
    public static boolean friendsHighlightSyncColor = true;
    public static List<String> friends = new ArrayList<>();

    // Halo
    public static boolean haloEnabled = false;
    public static BATclient_ConfigEnums.HaloSoulStyle haloSoulRenderSoulStyle = BATclient_ConfigEnums.HaloSoulStyle.SMOKE;
    public static int haloSoulLenght = 20;
    public static float haloSoulFactor = 1.0f;
    public static float haloSoulRadius = 1.0f;
    public static float haloSoulStartSize = 0.5f;
    public static float haloSoulEndSize = 0.0f;
    public static float haloSoulScale = 100;
    public static int haloSoulSubdivision = 16;

    // Hit Bubbles
    public static boolean hitBubblesEnabled = true;
    public static float hitBubblesRenderTime = 50;
    public static float hitBubblesScale = 100;
    public static BATclient_ConfigEnums.HitBubblesStyle hitBubblesStyle = BATclient_ConfigEnums.HitBubblesStyle.CIRCLE;

    // Hit Color
    public static boolean hitColorEnabled = true;
    public static boolean hitColorCustomColor = false;
    public static int hitColorColor = 0xFF0000;
    public static float hitColorAlpha = 50;

    // Hit Particles
    public static boolean hitParticlesEnabled = true;
    public static boolean hitParticlesCritOnly = false;
    public static int hitParticlesCount = 5;
    public static boolean hitParticlesLikeCrit = true;
    public static BATclient_ConfigEnums.HitParticlesTextMode hitParticlesTextMode = BATclient_ConfigEnums.HitParticlesTextMode.DISABLED;
    public static boolean hitParticlesTextShowHeal = true;
    public static boolean hitParticlesSelf = false;
    public static boolean hitParticleIncludeFirefly = true;
    public static boolean hitParticleIncludeDollar = true;
    public static boolean hitParticleIncludeSnowflake = true;
    public static boolean hitParticleIncludeHeart = true;
    public static boolean hitParticleIncludeStar = true;
    public static boolean hitParticleIncludeGlyphs = true;
    public static int hitParticlesSpeed = 5;
    public static boolean hitParticlesSplashSpawn = true;
    public static BATclient_ConfigEnums.HitParticlesPhysic hitParticlesPhysic = BATclient_ConfigEnums.HitParticlesPhysic.GRAVITY;
    public static float hitParticlesScale = 1.0f;
    public static float hitParticlesRenderTime = 2.0f;
    public static float hitParticlesTextScale = 1.0f;
    public static BATclient_ConfigEnums.HitParticlesDisappear hitParticlesDisappear = BATclient_ConfigEnums.HitParticlesDisappear.ALPHA;

    // Hit Sound
    public static boolean hitSoundEnabled = true;
    public static boolean hitSoundOnlyCrit = false;
    public static float hitSoundVolume = 100;
    public static boolean hitSoundRandomPitch = true;
    public static CustomSounds.SoundType hitSoundType = CustomSounds.SoundType.ON;
    public static boolean hitSoundOverwriteEnabled = false;
    public static float hitSoundOverwriteCritVolume = 100;
    public static float hitSoundOverwriteSweepVolume = 100;
    public static float hitSoundOverwriteNoDamageVolume = 100;
    public static float hitSoundOverwriteKnockbackVolume = 100;
    public static float hitSoundOverwriteStrongVolume = 100;
    public static float hitSoundOverwriteWeakVolume = 100;

    // HUD - Better Potions
    public static boolean hudBetterPotionsHudEnabled = true;
    public static int hudBetterPotionsHudX = 0;
    public static int hudBetterPotionsHudY = 0;
    public static boolean hudBetterPotionsHudToRoman = true;

    // HUD - Better Scoreboard
    public static boolean hudBetterScoreboardEnabled = true;
    public static boolean hudBetterScoreboardGlow = true;
    public static boolean hudBetterScoreboardColor = true;
    public static boolean hudBetterScoreboardDarker = true;

    // Jump Circles
    public static BATclient_ConfigEnums.JumpCirclesStyle jumpCirclesStyle = BATclient_ConfigEnums.JumpCirclesStyle.CIRCLE;
    public static boolean jumpParticlesEnabled = true;
    public static boolean jumpParticlesIncludeFirefly = true;
    public static boolean jumpParticlesIncludeDollar = true;
    public static boolean jumpParticlesIncludeSnowflake = true;
    public static boolean jumpParticlesIncludeHeart = true;
    public static boolean jumpParticlesIncludeStar = true;
    public static boolean jumpParticlesIncludeGlyphs = true;
    public static int jumpParticlesCount = 5;
    public static float jumpParticlesScale = 1.0f;
    public static float jumpParticlesLiveTime = 2.0f;
    public static int jumpParticlesSpeed = 5;
    public static BATclient_ConfigEnums.JumpParticlesPhysic jumpParticlesPhysic = BATclient_ConfigEnums.JumpParticlesPhysic.GRAVITY;
    public static BATclient_ConfigEnums.JumpParticlesDisappear jumpParticlesDisappear = BATclient_ConfigEnums.JumpParticlesDisappear.ALPHA;

    // MC Tiers
    public static boolean mctiersEnabled = true;
    public static BATclient_ConfigEnums.MCTiersGameMode mctiersGameMode = BATclient_ConfigEnums.MCTiersGameMode.VANILLA;

    // Mouse Move
    public static boolean mouseMoveEnabled = true;
    public static int mouseMoveX = 0;
    public static int mouseMoveY = 0;
    public static boolean mouseMoveBlur = true;

    // No Fire Overlay
    public static boolean noFireOverlayEnabled = false;

    // RPC
    public static boolean rpcEnabled = true;
    public static BATclient_ConfigEnums.RPCState rpcState = BATclient_ConfigEnums.RPCState.NAME;
    public static boolean noFireOverlayCustomColorEnabled = false;
    public static int noFireOverlayCustomColor = 0xFF8800;
    public static float noFireOverlayAlpha = 50;
    public static float noFireOverlayY = 0;

    public static String rpcCustomStateText = null;

    // Swing Hand
    public static boolean swingHandEnabled = false;
    public static float swingHand_xPos = 0.7f;
    public static float swingHand_yPos = -0.4f;
    public static float swingHand_zPos = -0.85f;
    public static float swingHand_scale = 0.75f;
    public static float swingHand_rotX = 0f;
    public static float swingHand_rotY = -13f;
    public static float swingHand_rotZ = 8f;
    public static float swingHand_xSwingRot = -55f;
    public static float swingHand_ySwingRot = 0f;
    public static float swingHand_zSwingRot = 90f;
    public static float swingHand_speed = 100f;

    // Target HUD
    public static boolean targetHudEnabled = true;
    public static int targetHudOffsetX = 0;
    public static int targetHudOffsetY = 0;
    public static float targetHudRenderTime = 100;
    public static BATclient_ConfigEnums.TargetHudStyle targetHudStyle = BATclient_ConfigEnums.TargetHudStyle.NORMAL;
    public static boolean targetHudFollow = false;
    public static float targetHudEntityOffsetX = 0;
    public static float targetHudEntityOffsetY = 0;
    public static boolean targetHudParticles = true;
    public static float targetHudParticleScale = 100;
    public static boolean targetHudIncludeFirefly = true;
    public static boolean targetHudIncludeDollar = true;
    public static boolean targetHudIncludeSnowflake = true;
    public static boolean targetHudIncludeHeart = true;
    public static boolean targetHudIncludeStar = true;
    public static boolean targetHudIncludeGlyphs = true;

    // Target Render
    public static boolean targetRenderEnabled = true;
    public static BATclient_ConfigEnums.TargetRenderStyle targetRenderStyle = BATclient_ConfigEnums.TargetRenderStyle.LEGACY;
    public static boolean targetRenderOnlyPlayers = false;
    public static float targetRenderLiveTime = 10.0f;

    // Target Render - Legacy
    public static BATclient_ConfigEnums.TargetRenderLegacyTexture targetRenderLegacyTexture = BATclient_ConfigEnums.TargetRenderLegacyTexture.LEGACY;
    public static float targetRenderLegacyScale = 100;
    public static float targetRenderLegacyAlpha = 100;
    public static float targetRenderLegacyRollSpeed = 100;

    // Target Render - Soul
    public static BATclient_ConfigEnums.TargetRenderSoulStyle targetRenderSoulStyle = BATclient_ConfigEnums.TargetRenderSoulStyle.SMOKE;
    public static BATclient_ConfigEnums.TargetRenderSoulTexture targetRenderSoulTexture = BATclient_ConfigEnums.TargetRenderSoulTexture.FIREFLY;
    public static int targetRenderSoulLenght = 20;
    public static float targetRenderSoulFactor = 1.0f;
    public static float targetRenderSoulShaking = 0;
    public static float targetRenderSoulAmplitude = 0;
    public static float targetRenderSoulRadius = 1.0f;
    public static float targetRenderSoulStartSize = 0.5f;
    public static float targetRenderSoulEndSize = 0.0f;
    public static float targetRenderSoulScale = 100;
    public static int targetRenderSoulSubdivision = 16;

    // Target Render - Topka
    public static float targetRenderTopkaSpeed = 100;
    public static float targetRenderTopkaRadius = 100;

    // Time Changer
    public static boolean timeChangerEnabled = false;
    public static float timeChangerTime = 50;

    // Totem Pop
    public static boolean totemPopShaderEnabled = false;
    public static float totemShaderAlpha = 100;
    public static boolean totemPopParticlesEnabled = true;
    public static boolean totemOverwriteScaleEnable = false;
    public static float totemOverwriteScale = 100;
    public static boolean totemPopDefaultColors = true;
    public static int totemPopParticlesCount = 10;
    public static boolean totemPopParticlesIncludeFirefly = true;
    public static boolean totemPopParticlesIncludeDollar = true;
    public static boolean totemPopParticlesIncludeSnowflake = true;
    public static boolean totemPopParticlesIncludeHeart = true;
    public static boolean totemPopParticlesIncludeStar = true;
    public static boolean totemPopParticlesIncludeGlyphs = true;
    public static int totemPopParticlesSpeed = 5;
    public static float totemPopParticlesScale = 1.0f;
    public static BATclient_ConfigEnums.TotemPopParticlesDisappear totemPopParticlesDisappear = BATclient_ConfigEnums.TotemPopParticlesDisappear.ALPHA;

    // Trails
    public static boolean trailsEnabled = true;
    public static int trailsLenght = 10;
    public static boolean trailsForGliders = true;
    public static boolean trailsFirstPerson = false;
    public static float trailsHeight = 100;
    public static BATclient_ConfigEnums.TrailsStyle trailsStyle = BATclient_ConfigEnums.TrailsStyle.FADED;
    public static boolean trailsRenderHalf = false;
    public static float trailsAlphaFactor = 100;

    // Trajectories
    public static boolean trajectoriesPreviewEnabled = false;
    public static boolean trajectoriesPreviewLandSideOutline = true;
    public static boolean trajectoriesPreviewLandSideFill = true;

    // Translator
    public static boolean translatorBruhEnabled = false;

    // Watermark
    public static boolean waterMarkEnabled = true;
    public static int waterMarkX = 0;
    public static int waterMarkY = 0;

    // Weather Changer
    public static boolean weatherChangerEnabled = false;
    public static String weatherType = "CLEAN";

    // Sky Color
    public static boolean coloredSkyEnabled = false;
}
