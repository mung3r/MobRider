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

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Pig;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;

import com.edwardhand.mobrider.ConfigManager;
import com.edwardhand.mobrider.MobRider;
import com.edwardhand.mobrider.goals.GoalManager;
import com.edwardhand.mobrider.rider.Rider;
import com.edwardhand.mobrider.rider.RiderManager;

public class RiderPlayerListener implements Listener
{
    private static final int FIRST_SLOT = 0;
    private static final int LAST_SLOT = 8;
    private static final float SPEED_INCREMENT = 0.05F;
    
    private ConfigManager configManager;
    private RiderManager riderManager;
    private GoalManager goalManager;

    public RiderPlayerListener(MobRider plugin)
    {
        configManager = plugin.getConfigManager();
        riderManager = plugin.getRiderManager();
        goalManager = plugin.getGoalManager();
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerInteract(PlayerInteractEvent event)
    {
        Player player = event.getPlayer();

        if (player.getItemInHand().getType() == configManager.controlItem && riderManager.isRider(player) && !riderManager.getRider(player).getGoal().isWithinHysteresisThreshold()) {
            if (event.getAction() == Action.LEFT_CLICK_AIR) {
                goalManager.setDirection(riderManager.getRider(player), player.getLocation().getDirection().normalize());
            }
            else if (event.getAction() == Action.RIGHT_CLICK_AIR) {
                goalManager.setStopGoal(riderManager.getRider(player));
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event)
    {
        Player player = event.getPlayer();
        Entity vehicle = player.getVehicle();
        Entity target = event.getRightClicked();

        if (vehicle instanceof LivingEntity && vehicle.equals(target) && riderManager.isRider(player)) {
            if (configManager.isFood(player.getItemInHand().getType())) {
                riderManager.feedRide(riderManager.getRider(player));
            }
            else {
                riderManager.removeRider(player);
            }

            if (vehicle instanceof Pig) {
                event.setCancelled(true);
            }
        }
        else if (player.getItemInHand().getType() == Material.SADDLE && RiderManager.canRide(player, target)) {
            target.setPassenger(player);
            goalManager.setStopGoal(riderManager.addRider(player));
        }
        else if (target instanceof Pig && ((Pig) target).hasSaddle() && RiderManager.canRide(player, target)) {
            target.setPassenger(player);
            goalManager.setStopGoal(riderManager.addRider(player));
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onItemHeldChange(PlayerItemHeldEvent event)
    {
        Player player = event.getPlayer();

        // Holding down SHIFT(sneak) and mouse scrolling adjusts speed
        if (riderManager.isRider(player) && player.isSneaking()) {
            int newSlot = event.getNewSlot();
            int prevSlot = event.getPreviousSlot();
            boolean increase = (prevSlot - newSlot) > 0;

            if (((prevSlot == FIRST_SLOT) && (newSlot == LAST_SLOT)) || ((prevSlot == LAST_SLOT) && (newSlot == FIRST_SLOT))) {
                increase = !increase;
            }

            Rider rider = riderManager.getRider(player);
            rider.setSpeed(increase ? rider.getSpeed() + SPEED_INCREMENT : rider.getSpeed() - SPEED_INCREMENT);
        }
    }
}
