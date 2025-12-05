package me.BATapp.batclient.discord.callback;

import com.sun.jna.Callback;

public interface DisconnectedCallback extends Callback {
    void apply(final int p0, final String p1);
}
