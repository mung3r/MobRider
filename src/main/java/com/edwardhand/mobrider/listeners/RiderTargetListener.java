package com.edwardhand.mobrider.listeners;

import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityListener;
import org.bukkit.event.entity.EntityTargetEvent;

import com.edwardhand.mobrider.MobRider;

public class RiderTargetListener extends EntityListener
{
    private MobRider plugin;

    public RiderTargetListener(MobRider plugin)
    {
        this.plugin = plugin;
    }

    public void onEntityTarget(EntityTargetEvent event)
    {
        if (event.isCancelled())
            return;

        net.minecraft.server.Entity passenger = ((CraftEntity) event.getEntity()).getHandle().passenger;
        Entity target = event.getTarget();

        if ((passenger != null) && (target != null) && (target.equals(passenger.getBukkitEntity())))
            event.setCancelled(true);
    }
}
