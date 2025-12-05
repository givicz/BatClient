package me.BATapp.batclient.interfaces;

import java.util.ArrayList;
import java.util.List;

public interface OverlayReloadListener {
    List<OverlayReloadListener> listeners = new ArrayList<>();
    void batclient$onOverlayReload();

    static void register(OverlayReloadListener listener) {
        listeners.add(listener);
    }

    static void callEvent() {
        List<OverlayReloadListener> copy = new ArrayList<>(listeners);
        for (OverlayReloadListener listener : copy) {
            listener.batclient$onOverlayReload();
        }
    }
}
