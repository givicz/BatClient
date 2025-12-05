package me.BATapp.batclient.discord.callback;

import com.sun.jna.Callback;

public interface SpectateGameCallback extends Callback {
    void apply(final String p0);
}
