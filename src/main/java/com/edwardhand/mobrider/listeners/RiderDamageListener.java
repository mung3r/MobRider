/*
 * This file is part of MobRider.
 *
 * Copyright (c) 2011-2012, R. Ramos <http://github.com/mung3r/>
 * MobRider is licensed under the GNU Lesser General Public License.
 *
 * MobRider is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MobRider is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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

import com.edwardhand.mobrider.MobRider;
import com.edwardhand.mobrider.goals.GoalManager;
import com.edwardhand.mobrider.rider.Rider;
import com.edwardhand.mobrider.rider.RiderManager;

public class RiderDamageListener implements Listener
{
    private RiderManager riderManager;
    private GoalManager goalManager;

    public RiderDamageListener(MobRider plugin)
    {
        riderManager = plugin.getRiderManager();
        goalManager = plugin.getGoalManager();
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event)
    {
        if (event.isCancelled()) {
            return;
        }

        Entity entity = event.getEntity();
        Entity damager = event.getDamager();

        if (damager instanceof Projectile) {
            damager = ((Projectile) damager).getShooter();
        }

        // rider damaged by entity
        if (entity instanceof Player) {

            Rider rider = riderManager.getRider((Player) entity);

            if (rider.isValid()) {
                LivingEntity ride = rider.getRide();

                if (damager.equals(ride) && !(damager instanceof Player)) {
                    event.setCancelled(true); // riders get in the way of
                                              // skeleton arrows
                }
                else if (!damager.equals(ride) && !damager.equals(rider.getTarget())) {
                    goalManager.setAttackGoal(rider, (LivingEntity) damager);
                    return;
                }
            }
        }

        // entity damaged by rider
        if (damager instanceof Player) {

            Rider rider = riderManager.getRider((Player) damager);

            if (rider.isValid()) {
                LivingEntity ride = rider.getRide();

                if (!entity.equals(ride) && !entity.equals(rider.getTarget())) {
                    goalManager.setAttackGoal(rider, (LivingEntity) entity);
                    return;
                }
            }
        }

        // ride damaged by entity
        if (damager instanceof LivingEntity && entity.getPassenger() instanceof Player) {
            Player player = (Player) entity.getPassenger();
            Rider rider = riderManager.getRider((Player) entity.getPassenger());

            if (rider.isValid()) {
                if (!damager.equals(player)) {
                    goalManager.setAttackGoal(rider, (LivingEntity) damager);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntityDamageByBlock(EntityDamageByBlockEvent event)
    {
        Entity entity = event.getEntity();

        if (entity instanceof Player) {
            // rider damaged by drowning or suffocation
            Rider rider = riderManager.getRider((Player) entity);

            if (rider.isValid()) {
                switch (event.getCause()) {
                    case SUFFOCATION:
                        event.setCancelled(true);
                        break;
                    case DROWNING:
                        if (rider.hasWaterCreature()) {
                            event.setCancelled(true);
                        }
                        break;
                    default:
                        break;
                }
            }
        }
    }
}
