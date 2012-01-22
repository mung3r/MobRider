package com.edwardhand.mobrider.listeners;

import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTargetEvent;

public class RiderTargetListener implements Listener
{
    public RiderTargetListener()
    {
    }

    @EventHandler(event = EntityTargetEvent.class)
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
