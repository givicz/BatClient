package me.BATapp.batclient.mixin.inject;

import me.BATapp.batclient.interfaces.TrailEntity;
import me.BATapp.batclient.modules.Trails;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.ArrayList;
import java.util.List;

@Mixin(Entity.class)
public abstract class RegisterEntityTrails implements TrailEntity {

    @Override
    public List<Trails.TrailSegment> batclient$getTrails() {
        return trails;
    }

    @Unique
    public List<Trails.TrailSegment> trails = new ArrayList<>();
}
