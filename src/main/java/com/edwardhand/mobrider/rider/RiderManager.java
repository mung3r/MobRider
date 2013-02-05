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
package com.edwardhand.mobrider.rider;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import net.milkbowl.vault.economy.EconomyResponse;

import org.bukkit.Bukkit;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Bat;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Ghast;
import org.bukkit.entity.Golem;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.entity.Slime;
import org.bukkit.entity.Squid;
import org.bukkit.entity.Tameable;
import org.bukkit.entity.Villager;
import org.bukkit.metadata.FixedMetadataValue;

import com.edwardhand.mobrider.ConfigManager;
import com.edwardhand.mobrider.MobRider;
import com.edwardhand.mobrider.commons.DependencyUtils;
import com.edwardhand.mobrider.commons.EntityUtils;
import com.edwardhand.mobrider.commons.LoggerUtil;
import com.edwardhand.mobrider.commons.MessageUtils;
import com.edwardhand.mobrider.goals.GoalManager;
import com.edwardhand.mobrider.metrics.RideMetrics;

public class RiderManager implements Runnable
{
    private static final String SPAWNEGG_TAG_MDID = "MobRider.spawnEgg";
    private static final long UPDATE_DELAY = 0L;
    private static final long MAX_UPDATE_PERIOD = 20L;
    private static final int HEALTH_INCREMENT = 5;
    private static Random random = new Random();

    private MobRider plugin;
    private RideMetrics metrics;
    private ConfigManager configManager;
    private GoalManager goalManager;
    private Map<String, Rider> riders;

    private final FixedMetadataValue spawnEggTag;
    
    public RiderManager(MobRider plugin)
    {
        this.plugin = plugin;
        metrics = plugin.getMetricsManager();
        configManager = plugin.getConfigManager();
        goalManager = plugin.getGoalManager();

        riders = new ConcurrentHashMap<String, Rider>();
        spawnEggTag = new FixedMetadataValue(plugin, true);

        if (Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, this, UPDATE_DELAY, Math.min(configManager.updatePeriod, MAX_UPDATE_PERIOD)) < 0) {
            Bukkit.getPluginManager().disablePlugin(plugin);
            LoggerUtil.getInstance().severe("Failed to schedule RiderManager task.");
        }
    }

    @Override
    public void run()
    {
        for (Entry<String, Rider> entry : riders.entrySet()) {
            String playerName = entry.getKey();
            Rider rider = entry.getValue();

            if (rider.isValid()) {
                goalManager.update(rider);
            }
            else {
                rider.setTarget(null);
                riders.remove(playerName);
            }
        }
    }

    public Rider addRider(Player player)
    {
        Rider rider = null;

        if (player != null) {
            String playerName = player.getName();
            rider = new Rider(playerName);
            goalManager.setStopGoal(rider);
            riders.put(playerName, rider);

            if (rider.getRide() != null) {
                metrics.addCount(rider.getRide().getType());
            }
        }

        return rider != null ? rider : new Rider(null);
    }

    public void removeRider(Player player)
    {
        Entity entity = player.getVehicle();

        if (entity instanceof LivingEntity) {
            LivingEntity target = (LivingEntity) entity;
            player.eject();
            if (DependencyUtils.hasPermission(player, "mobrider.spawnegg")) {
                scheduleSpawnEgg(target);
            }
        }
    }

    private void scheduleSpawnEgg(LivingEntity target)
    {
        synchronized (target) {
            if (hasSpawnEgg(target) && !target.hasMetadata(SPAWNEGG_TAG_MDID)) {
                target.setMetadata(SPAWNEGG_TAG_MDID, spawnEggTag);
                SpawnEggTask spawnEggTask = new SpawnEggTask(target);
                spawnEggTask.setTaskId(Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, spawnEggTask, 0, 1));
            }
        }
    }

    private static boolean hasSpawnEgg(Entity entity)
    {
        boolean hasSpawnEgg = false;

        if (entity instanceof LivingEntity) {
            EntityType type = ((LivingEntity) entity).getType();
            switch (type) {
                case ENDER_DRAGON:
                case GIANT:
                case SNOWMAN:
                case IRON_GOLEM:
                case PLAYER:
                    hasSpawnEgg = false;
                    break;
                default:
                    hasSpawnEgg = true;
            }
        }

        return hasSpawnEgg;
    }

    public Rider getRider(Player player)
    {
        Rider rider = null;

        if (player != null) {
            rider = riders.get(player.getName());
        }

        return rider != null ? rider : new Rider(null);
    }

    public boolean isRider(Player player)
    {
        if (player != null) {
            Entity vehicle = player.getVehicle();
            if (vehicle instanceof LivingEntity) {
                if (!riders.containsKey(player.getName())) {
                    addRider(player);
                }
                return true;
            }
        }
        return false;
    }

    public void feedRide(Rider rider)
    {
        if (rider.isFullHealth()) {
            MessageUtils.sendMessage(rider, configManager.fedConfusedMessage);
        }
        else {
            rider.setHealth(Math.min(rider.getHealth() + HEALTH_INCREMENT, rider.getMaxHealth()));
            EntityUtils.removeItemInHand(rider.getPlayer());
            MessageUtils.sendMessage(rider, configManager.fedConfirmedMessage);
        }
    }

    public static boolean canRide(Player player, Entity entity)
    {
        return isAllowed(player, entity) && (player.isOp() || isOwner(player, entity) || isWinner(player, entity) && isWithdrawSuccess(player, entity));
    }

    private static boolean isAllowed(Player player, Entity entity)
    {
        if (player == null || entity == null) {
            return false;
        }

        if (entity.getPassenger() != null) {
            player.sendMessage("That creature already has a rider.");
            return false;
        }

        if (entity instanceof Animals || entity instanceof Squid || entity instanceof Golem || entity instanceof Villager || entity instanceof Bat) {
            if (DependencyUtils.hasPermission(player, "mobrider.animals") || DependencyUtils.hasPermission(player, "mobrider.animals." + EntityUtils.getCreatureName(entity).toLowerCase())) {
                return true;
            }
            else {
                player.sendMessage("You do not have permission to ride that animal.");
                return false;
            }
        }

        if (entity instanceof Monster || entity instanceof Ghast || entity instanceof Slime || entity instanceof EnderDragon) {
            if (DependencyUtils.hasPermission(player, "mobrider.monsters") || DependencyUtils.hasPermission(player, "mobrider.monsters." + EntityUtils.getCreatureName(entity).toLowerCase())) {
                return true;
            }
            else {
                player.sendMessage("You do not have permission to ride that monster.");
                return false;
            }
        }

        if (entity instanceof Player) {
            if (DependencyUtils.hasPermission(player, "mobrider.players") || DependencyUtils.hasPermission(player, "mobrider.players." + ((Player) entity).getName().toLowerCase())) {
                return true;
            }
            else {
                player.sendMessage("You do not have permission to ride that player.");
                return false;
            }
        }

        return false;
    }

    private static boolean isWinner(Player player, Entity entity)
    {
        boolean isWinner = RideType.fromType(entity.getType()) != null ? random.nextDouble() * 100D < RideType.fromType(entity.getType()).getChance() : true;

        if (!isWinner && player != null) {
            player.sendMessage("You have bad luck trying to ride that creature.");
        }

        return isWinner;
    }

    private static boolean isWithdrawSuccess(Player player, Entity entity)
    {
        if (!DependencyUtils.hasEconomy()) {
            return true;
        }

        if (player == null || entity == null) {
            return false;
        }

        double cost = RideType.fromType(entity.getType()).getCost();

        if (cost == 0.0) {
            return true;
        }

        EconomyResponse response = DependencyUtils.getEconomy().withdrawPlayer(player.getName(), cost);
        if (response.transactionSuccess()) {
            player.sendMessage("You were charged " + DependencyUtils.getEconomy().format(response.amount) + " for riding this creature.");
        }
        else {
            player.sendMessage("You have insufficient funds to ride that creature.");
        }

        return response.transactionSuccess();
    }

    private static boolean isOwner(Player player, Entity entity)
    {
        if (entity instanceof Tameable) {
            Tameable tameable = (Tameable) entity;

            if (tameable.isTamed() && tameable.getOwner() instanceof Player) {
                Player owner = (Player) tameable.getOwner();
                if (owner.getName().equals(player.getName())) {
                    return true;
                }
            }
        }
        return false;
    }
}
