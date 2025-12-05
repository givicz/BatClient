package me.BATapp.batclient.modules;

import me.BATapp.batclient.SoupModule;
import me.BATapp.batclient.screen.CapeSelectScreen;
import me.BATapp.batclient.settings.impl.BooleanSetting;
import me.BATapp.batclient.settings.impl.ButtonSetting;
import me.BATapp.batclient.settings.impl.EnumSetting;
import me.BATapp.batclient.settings.impl.StringSetting;
import net.minecraft.util.Identifier;

public class Capes extends SoupModule {
    public static BooleanSetting enabled = new BooleanSetting("Enabled", "desc", false);
    @SuppressWarnings("unused")
    public static ButtonSetting capeSelectButton = new ButtonSetting("Select Cape", "desc", () -> mc.setScreen(new CapeSelectScreen()));
    public static final EnumSetting<CapeTextures> cape = new EnumSetting<>("Cape", "description", CapeTextures.CUSTOM_JAVA, CapeTextures.class);

    public static BooleanSetting useCustom = new BooleanSetting("Custom", "desc", false);
    public static StringSetting link = new StringSetting("Link", "dessdas", "https://static.wikia.nocookie.net/minecraft_gamepedia/images/8/86/MINECON_2019_Cape_%28texture%29.png/revision/latest?cb=20220316113812");

    public Capes() {
        super("Capes", Category.OTHER);
    }

    public enum Config {
        SELECT_CAPE, UPDATE_CAPE
    }

    public enum CapeTextures {
        // April Fools Caeps
        APRIL_AWESOME_CAEP(Identifier.of("batclient:textures/capes/april/awesome_caep.png")),
        APRIL_BLONK_CAEP(Identifier.of("batclient:textures/capes/april/blonk_caep.png")),
        APRIL_NO_CIRCLE_CAEP(Identifier.of("batclient:textures/capes/april/no_circle_caep.png")),
        APRIL_NYAN_CAEP(Identifier.of("batclient:textures/capes/april/nyan_caep.png")),
        APRIL_SQUID_CAEP(Identifier.of("batclient:textures/capes/april/squid_caep.png")),
        APRIL_VETERINARIAN_CAEP(Identifier.of("batclient:textures/capes/april/veterinarian_caep.png")),

        // Default Capes
        DEFAULT_15TH_ANNIVERSARY(Identifier.of("batclient:textures/capes/default/15th_anniversary.png")),
        DEFAULT_BACON(Identifier.of("batclient:textures/capes/default/bacon.png")),
        DEFAULT_BIRTHDAY(Identifier.of("batclient:textures/capes/default/birthday.png")),
        DEFAULT_CHERRY_BLOSSOM(Identifier.of("batclient:textures/capes/default/cherry_blossom.png")),
        DEFAULT_COBALT(Identifier.of("batclient:textures/capes/default/cobalt.png")),
        DEFAULT_DANNY_BSTYLE(Identifier.of("batclient:textures/capes/default/danny_bstyle.png")),
        DEFAULT_FOLLOWERS(Identifier.of("batclient:textures/capes/default/followers.png")),
        DEFAULT_MCC_15TH_YEARS(Identifier.of("batclient:textures/capes/default/mcc_15th_years.png")),
        DEFAULT_MIGRATOR(Identifier.of("batclient:textures/capes/default/migrator.png")),
        DEFAULT_MILLIONTH_CUSTOMER(Identifier.of("batclient:textures/capes/default/millionth_customer.png")),
        DEFAULT_MINECON_2011(Identifier.of("batclient:textures/capes/default/minecon_2011.png")),
        DEFAULT_MINECON_2012(Identifier.of("batclient:textures/capes/default/minecon_2012.png")),
        DEFAULT_MINECON_2013(Identifier.of("batclient:textures/capes/default/minecon_2013.png")),
        DEFAULT_MINECON_2014(Identifier.of("batclient:textures/capes/default/minecon_2014.png")),
        DEFAULT_MINECON_2015(Identifier.of("batclient:textures/capes/default/minecon_2015.png")),
        DEFAULT_MINECON_2016(Identifier.of("batclient:textures/capes/default/minecon_2016.png")),
        DEFAULT_MINECON_2017(Identifier.of("batclient:textures/capes/default/minecon_2017.png")),
        DEFAULT_MINECON_2018(Identifier.of("batclient:textures/capes/default/minecon_2018.png")),
        DEFAULT_MINECON_2019(Identifier.of("batclient:textures/capes/default/minecon_2019.png")),
        DEFAULT_MINECRAFT_EXPERIENCE(Identifier.of("batclient:textures/capes/default/minecraft_experience.png")),
        DEFAULT_MOJANG(Identifier.of("batclient:textures/capes/default/mojang.png")),
        DEFAULT_MOJANG_OLD(Identifier.of("batclient:textures/capes/default/mojang_old.png")),
        DEFAULT_MOJANG_STUDIOS(Identifier.of("batclient:textures/capes/default/mojang_studios.png")),
        DEFAULT_MOJIRA_MODERATOR(Identifier.of("batclient:textures/capes/default/mojira_moderator.png")),
        DEFAULT_NEW_YEARS_2010(Identifier.of("batclient:textures/capes/default/new_years_2010.png")),
        DEFAULT_NEW_YEARS_2011(Identifier.of("batclient:textures/capes/default/new_years_2011.png")),
        DEFAULT_PANCAKE(Identifier.of("batclient:textures/capes/default/pancake.png")),
        DEFAULT_PRISMARINE(Identifier.of("batclient:textures/capes/default/prismarine.png")),
        DEFAULT_PURPLE_HEART(Identifier.of("batclient:textures/capes/default/purple_heart.png")),
        DEFAULT_REALMS_NEW(Identifier.of("batclient:textures/capes/default/realms_new.png")),
        DEFAULT_REALMS_OLD(Identifier.of("batclient:textures/capes/default/realms_old.png")),
        DEFAULT_SCROLLS(Identifier.of("batclient:textures/capes/default/scrolls.png")),
        DEFAULT_SNOWMAN(Identifier.of("batclient:textures/capes/default/snowman.png")),
        DEFAULT_SPADE(Identifier.of("batclient:textures/capes/default/spade.png")),
        DEFAULT_TEST(Identifier.of("batclient:textures/capes/default/test.png")),
        DEFAULT_TRANSLATOR(Identifier.of("batclient:textures/capes/default/translator.png")),
        DEFAULT_TURTLE(Identifier.of("batclient:textures/capes/default/turtle.png")),
        DEFAULT_UNUSED_1(Identifier.of("batclient:textures/capes/default/unused_1.png")),
        DEFAULT_UNUSED_2(Identifier.of("batclient:textures/capes/default/unused_2.png")),
        DEFAULT_UNUSED_3(Identifier.of("batclient:textures/capes/default/unused_3.png")),
        DEFAULT_VALENTINE(Identifier.of("batclient:textures/capes/default/valentine.png")),
        DEFAULT_VANILLA(Identifier.of("batclient:textures/capes/default/vanilla.png")),
        DEFAULT_MENACE(Identifier.of("batclient:textures/capes/default/menace.png")),
        DEFAULT_HOME(Identifier.of("batclient:textures/capes/default/home.png")),
        DEFAULT_OFFICE(Identifier.of("batclient:textures/capes/default/office.png")),
        DEFAULT_YEARN(Identifier.of("batclient:textures/capes/default/yearn.png")),
        DEFAULT_COMMON(Identifier.of("batclient:textures/capes/default/common.png")),

        // Microsoft(XBox) capes
        XBOX_XBOX(Identifier.of("batclient:textures/capes/microsoft/xbox.png")),
        XBOX_1ST_BIRTHDAY(Identifier.of("batclient:textures/capes/microsoft/xbox_1st_birthday.png")),
        XBOX_UNUSED_STARWARS_1(Identifier.of("batclient:textures/capes/microsoft/unused_starwars_1.png")),
        XBOX_UNUSED_STARWARS_2(Identifier.of("batclient:textures/capes/microsoft/unused_starwars_2.png")),
        XBOX_ADVENTURE_TIME_UNUSED(Identifier.of("batclient:textures/capes/microsoft/adventuretime_unused.png")),

        // Minecraft dungeons capes
        MCD_AMETHYST(Identifier.of("batclient:textures/capes/mcd/amethyst.png")),
        MCD_CLOUDY_CLIMB(Identifier.of("batclient:textures/capes/mcd/cloudy_climb.png")),
        MCD_COW(Identifier.of("batclient:textures/capes/mcd/cow.png")),
        MCD_DOWNPOUR(Identifier.of("batclient:textures/capes/mcd/downpour.png")),
        MCD_FAUNA_FAIRE(Identifier.of("batclient:textures/capes/mcd/fauna_faire.png")),
        MCD_GIFT_WARP(Identifier.of("batclient:textures/capes/mcd/gift_warp.png")),
        MCD_GLOW(Identifier.of("batclient:textures/capes/mcd/glow.png")),
        MCD_HAMMER(Identifier.of("batclient:textures/capes/mcd/hammer.png")),
        MCD_HERO(Identifier.of("batclient:textures/capes/mcd/hero.png")),
        MCD_ICOLOGER(Identifier.of("batclient:textures/capes/mcd/icologer.png")),
        MCD_LUMINOUS_NIGHT(Identifier.of("batclient:textures/capes/mcd/luminous_night.png")),
        MCD_PHANTOM(Identifier.of("batclient:textures/capes/mcd/phantom.png")),
        MCD_SINISTER(Identifier.of("batclient:textures/capes/mcd/sinister.png")),
        MCD_TURTLE(Identifier.of("batclient:textures/capes/mcd/turtle.png")),

        // Custom Capes
        CUSTOM_1UP(Identifier.of("batclient:textures/capes/custom/1up.png")),
        CUSTOM_ADIDAS(Identifier.of("batclient:textures/capes/custom/adidas.png")),
        CUSTOM_ALPHA(Identifier.of("batclient:textures/capes/custom/alpha.png")),
        CUSTOM_AZURE(Identifier.of("batclient:textures/capes/custom/azure.png")),
        CUSTOM_BLUE_FLAME(Identifier.of("batclient:textures/capes/custom/blue_flame.png")),
        CUSTOM_BROWN_FEATHER(Identifier.of("batclient:textures/capes/custom/brown_feather.png")),
        CUSTOM_BUGGED(Identifier.of("batclient:textures/capes/custom/bugged.png")),
        CUSTOM_CB(Identifier.of("batclient:textures/capes/custom/cb.png")),
        CUSTOM_CHEST(Identifier.of("batclient:textures/capes/custom/chest.png")),
        CUSTOM_CHRISTMAS_LUNAR(Identifier.of("batclient:textures/capes/custom/christmas_lunar.png")),
        CUSTOM_DARK_GOLD(Identifier.of("batclient:textures/capes/custom/dark_gold.png")),
        CUSTOM_DISCORD(Identifier.of("batclient:textures/capes/custom/discord.png")),
        CUSTOM_FABRIC(Identifier.of("batclient:textures/capes/custom/fabric.png")),
        CUSTOM_GALAXY(Identifier.of("batclient:textures/capes/custom/galaxy.png")),
        CUSTOM_GAMEBOY(Identifier.of("batclient:textures/capes/custom/gameboy.png")),
        CUSTOM_GENDERFLUID(Identifier.of("batclient:textures/capes/custom/genderfluid.png")),
        CUSTOM_GRAY(Identifier.of("batclient:textures/capes/custom/gray.png")),
        CUSTOM_HALLOWEEN(Identifier.of("batclient:textures/capes/custom/halloween.png")),
        CUSTOM_ICE(Identifier.of("batclient:textures/capes/custom/ice.png")),
        CUSTOM_JAVA(Identifier.of("batclient:textures/capes/custom/java.png")),
        CUSTOM_JUKE_BOX(Identifier.of("batclient:textures/capes/custom/juke_box.png")),
        CUSTOM_KIRBY(Identifier.of("batclient:textures/capes/custom/kirby.png")),
        CUSTOM_MATRIX(Identifier.of("batclient:textures/capes/custom/matrix.png")),
        CUSTOM_NASA(Identifier.of("batclient:textures/capes/custom/nasa.png")),
        CUSTOM_NETHERITE(Identifier.of("batclient:textures/capes/custom/netherite.png")),
        CUSTOM_OMEGA_1(Identifier.of("batclient:textures/capes/custom/omega_1.png")),
        CUSTOM_OMEGA_2(Identifier.of("batclient:textures/capes/custom/omega_2.png")),
        CUSTOM_OMEGA_3(Identifier.of("batclient:textures/capes/custom/omega_3.png")),
        CUSTOM_ORANGE(Identifier.of("batclient:textures/capes/custom/orange.png")),
        CUSTOM_PURPLE(Identifier.of("batclient:textures/capes/custom/purple.png")),
        CUSTOM_RAIN(Identifier.of("batclient:textures/capes/custom/rain.png")),
        CUSTOM_ROSE(Identifier.of("batclient:textures/capes/custom/rose.png")),
        CUSTOM_SMOOTH_BLUE(Identifier.of("batclient:textures/capes/custom/smooth_blue.png")),
        CUSTOM_SNOWFLAKE_LUNAR(Identifier.of("batclient:textures/capes/custom/snowflake_lunar.png")),
        CUSTOM_SPEEDSILVER(Identifier.of("batclient:textures/capes/custom/speedsilver.png")),
        CUSTOM_VETERAN(Identifier.of("batclient:textures/capes/custom/veteran.png")),
        CUSTOM_WINTER(Identifier.of("batclient:textures/capes/custom/winter.png")),
        CUSTOM_YOUTUBE(Identifier.of("batclient:textures/capes/custom/youtube.png")),

        // Optifine
        OPTIFINE_STANDARD(Identifier.of("batclient:textures/capes/optifine/standard.png")),
        OPTIFINE_BLACK(Identifier.of("batclient:textures/capes/optifine/black.png")),
        OPTIFINE_BLUE(Identifier.of("batclient:textures/capes/optifine/blue.png")),
        OPTIFINE_CYAN(Identifier.of("batclient:textures/capes/optifine/cyan.png")),
        OPTIFINE_GRAY(Identifier.of("batclient:textures/capes/optifine/gray.png")),
        OPTIFINE_GREEN(Identifier.of("batclient:textures/capes/optifine/green.png")),
        OPTIFINE_PURPLE(Identifier.of("batclient:textures/capes/optifine/purple.png")),
        OPTIFINE_RED(Identifier.of("batclient:textures/capes/optifine/red.png")),
        OPTIFINE_YELLOW(Identifier.of("batclient:textures/capes/optifine/yellow.png")),
        OPTIFINE_WHITE(Identifier.of("batclient:textures/capes/optifine/white.png")),

        // Staff Capes by winvi
        STAFF_TEAM_PADEJ(Identifier.of("batclient:textures/capes/team/padej_.png")),
        STAFF_TEAM_WINVI(Identifier.of("batclient:textures/capes/team/winvi.png")),
        STAFF_TEAM_ICYCROW(Identifier.of("batclient:textures/capes/team/icycrow.png")),
        STAFF_TEAM_NOVADVORGA(Identifier.of("batclient:textures/capes/team/novadvorga.png")),
        STAFF_TEAM_IBRRPATAPIM_(Identifier.of("batclient:textures/capes/team/ibrrpatapim_.png"));

        private final Identifier texturePath;

        CapeTextures(Identifier texturePath) {
            this.texturePath = texturePath;
        }

        public Identifier getTexturePath() {
            return texturePath;
        }
    }
}
