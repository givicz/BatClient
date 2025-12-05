package me.BATapp.batclient.sounds;

import me.BATapp.batclient.utils.EntityUtils;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

import java.util.Random;

public class CustomSounds {
    public static SoundEvent ON = SoundEvent.of(Identifier.of("batclient", "on"));
    public static SoundEvent OFF = SoundEvent.of(Identifier.of("batclient", "off"));
    public static SoundEvent GET = SoundEvent.of(Identifier.of("batclient", "get"));
    public static SoundEvent BUBBLE = SoundEvent.of(Identifier.of("batclient", "bubble"));
    public static SoundEvent BELL = SoundEvent.of(Identifier.of("batclient", "bell"));
    public static SoundEvent BONK = SoundEvent.of(Identifier.of("batclient", "bonk"));
    public static SoundEvent POK = SoundEvent.of(Identifier.of("batclient", "pok"));
    public static SoundEvent MAGIC_POK = SoundEvent.of(Identifier.of("batclient", "magic_pok"));

    public enum SoundType {
        ON,
        OFF,
        GET,
        BUBBLE,
        BELL,
        BONK,
        POK,
        MAGIC_POK;
    }
}
