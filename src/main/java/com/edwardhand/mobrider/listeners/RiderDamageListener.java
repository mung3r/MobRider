package com.edwardhand.mobrider.listeners;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityListener;

import com.edwardhand.mobrider.MobRider;
import com.edwardhand.mobrider.models.Ride;

public class RiderDamageListener extends EntityListener
{
    private MobRider plugin;

    public RiderDamageListener(MobRider plugin)
    {
        this.plugin = plugin;
    }

    public void onEntityDamage(EntityDamageEvent event)
    {
        if (event.isCancelled())
            return;

        if (event instanceof EntityDamageByEntityEvent) {

            Entity rider = event.getEntity();
            Entity damager = ((EntityDamageByEntityEvent) event).getDamager();

            // rider damaged by entity
            if (rider instanceof Player && damager instanceof LivingEntity) {

                Ride mount = plugin.getRideHandler().getRide(rider);
                if (mount.isCreature()) {

                    if (!damager.equals(mount.getBukkitEntity()) && !damager.equals(mount.getTarget())) {
                        mount.attack((LivingEntity) damager);
                    }
                }
            }
            // rider damaged by player
            else if (rider instanceof LivingEntity && damager instanceof Player) {

                Ride mount = plugin.getRideHandler().getRide(damager);
                if (mount.isCreature()) {

                    if (!rider.equals(mount.getBukkitEntity()) && !rider.equals(mount.getTarget())) {
                        mount.attack((LivingEntity) rider);
                    }
                }
            }
        }
        // rider damaged by drowning or suffocation
        else if (event instanceof EntityDamageByBlockEvent) {

            Entity rider = event.getEntity();
            Ride vehicle = plugin.getRideHandler().getRide(rider);

            if (rider instanceof Player && vehicle.isCreature()) {
                switch (event.getCause()) {
                    case SUFFOCATION:
                        event.setCancelled(true);
                        break;
                    case DROWNING:
                        if (vehicle.isWaterCreature())
                            event.setCancelled(true);
                }
            }
        }
    }
}
