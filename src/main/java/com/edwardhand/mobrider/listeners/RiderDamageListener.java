package com.edwardhand.mobrider.listeners;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

import com.edwardhand.mobrider.MobRider;
import com.edwardhand.mobrider.models.Rider;
import com.edwardhand.mobrider.utils.MRHandler;

public class RiderDamageListener implements Listener
{
    private MRHandler riderHandler;

    public RiderDamageListener(MobRider plugin)
    {
        riderHandler = plugin.getRiderHandler();
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntityDamage(EntityDamageEvent event)
    {
        if (event.isCancelled())
            return;

        if (event instanceof EntityDamageByEntityEvent) {

            Entity entity = event.getEntity();
            Entity damager = ((EntityDamageByEntityEvent) event).getDamager();

            if (damager instanceof Projectile) {
                damager = ((Projectile) damager).getShooter();
            }

            // rider damaged by entity
            if (entity instanceof Player) {

                Rider rider = riderHandler.getRider((Player) entity);

                if (rider.isValid()) {
                    LivingEntity ride = rider.getRide();

                    if (damager.equals(ride)) {
                        event.setCancelled(true); // riders get in the way of skeleton arrows
                    }
                    else if (!damager.equals(ride) && !damager.equals(rider.getTarget())) {
                        rider.attack((LivingEntity) damager);
                        return;
                    }
                }
            }

            // entity damaged by rider
            if (damager instanceof Player) {

                Rider rider = riderHandler.getRider((Player) damager);

                if (rider.isValid()) {
                    LivingEntity ride = rider.getRide();

                    if (!entity.equals(ride) && !entity.equals(rider.getTarget())) {
                        rider.attack((LivingEntity) entity);
                        return;
                    }
                }
            }

            // ride damaged by entity
            if (damager instanceof LivingEntity) {
                Rider rider = riderHandler.getRider((Player) entity.getPassenger());

                if (rider.isValid()) {
                    if (!damager.equals(rider.getPlayer())) {
                        rider.attack((LivingEntity) damager);
                    }
                }
            }
        }
        // rider damaged by drowning or suffocation
        else if (event instanceof EntityDamageByBlockEvent) {

            Entity entity = event.getEntity();

            if (entity instanceof Player) {
                Rider rider = riderHandler.getRider((Player) entity);

                if (rider.isValid()) {
                    switch (event.getCause()) {
                        case SUFFOCATION:
                            event.setCancelled(true);
                            break;
                        case DROWNING:
                            if (rider.hasWaterCreature())
                                event.setCancelled(true);
                    }
                }
            }
        }
    }
}
