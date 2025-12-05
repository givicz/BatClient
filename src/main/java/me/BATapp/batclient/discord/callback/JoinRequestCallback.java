package me.BATapp.batclient.discord.callback;

import com.sun.jna.Callback;
import me.BATapp.batclient.discord.DiscordUser;

public interface JoinRequestCallback extends Callback {
    void apply(final DiscordUser p0);
}
