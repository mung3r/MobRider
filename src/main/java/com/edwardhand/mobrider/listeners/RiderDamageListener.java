package com.edwardhand.mobrider.listeners;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
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

            Entity entity = event.getEntity();
            Entity damager = ((EntityDamageByEntityEvent) event).getDamager();
            
            if (damager instanceof Projectile) {
                damager = ((Projectile) damager).getShooter();
            }

            // rider damaged by entity
            if (entity instanceof Player) {

                Ride ride = plugin.getRideHandler().getRide(entity);

                if (ride.hasRider()) {
                    if (!damager.equals(ride.getBukkitEntity()) && !damager.equals(ride.getTarget())) {
                        ride.attack((LivingEntity) damager);
                        return;
                    }
                }
            }

            // entity damaged by rider
            if (damager instanceof Player) {

                Ride ride = plugin.getRideHandler().getRide(damager);

                if (ride.hasRider()) {
                    if (!entity.equals(ride.getBukkitEntity()) && !entity.equals(ride.getTarget())) {
                        ride.attack((LivingEntity) entity);
                        return;
                    }
                }
            }

            // ride damaged by entity
            if (damager instanceof LivingEntity) {
                Ride ride = plugin.getRideHandler().getRide(entity.getPassenger());

                if (ride.hasRider()) {
                    if (!damager.equals(ride.getRider().getBukkitEntity())) {
                        ride.attack((LivingEntity) damager);
                    }
                }
            }
        }
        // rider damaged by drowning or suffocation
        else if (event instanceof EntityDamageByBlockEvent) {

            Entity rider = event.getEntity();
            Ride ride = plugin.getRideHandler().getRide(rider);

            if (rider instanceof Player && ride.getBukkitEntity() instanceof LivingEntity) {
                switch (event.getCause()) {
                    case SUFFOCATION:
                        event.setCancelled(true);
                        break;
                    case DROWNING:
                        if (ride.isWaterCreature())
                            event.setCancelled(true);
                }
            }
        }
    }
}
