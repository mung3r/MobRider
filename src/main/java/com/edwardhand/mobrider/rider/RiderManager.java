package com.edwardhand.mobrider.rider;

import java.util.Hashtable;
import java.util.Map;
import java.util.Random;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Animals;
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

import com.edwardhand.mobrider.ConfigManager;
import com.edwardhand.mobrider.MobRider;
import com.edwardhand.mobrider.commons.DependencyUtils;
import com.edwardhand.mobrider.commons.EntityUtils;
import com.edwardhand.mobrider.commons.MRLogger;
import com.edwardhand.mobrider.commons.MessageUtils;
import com.edwardhand.mobrider.goals.GoalManager;
import com.edwardhand.mobrider.metrics.MetricsManager;

public class RiderManager implements Runnable
{
    private static final long UPDATE_DELAY = 0L;
    private static final long MAX_UPDATE_PERIOD = 20L;
    private static Random random = new Random();

    private MobRider plugin;
    private Economy economy;
    private MetricsManager metrics;
    private ConfigManager configManager;
    private GoalManager goalManager;
    private Map<String, Rider> riders;

    public RiderManager(MobRider plugin)
    {
        this.plugin = plugin;
        economy = DependencyUtils.getEconomy();
        metrics = plugin.getMetricsManager();
        configManager = plugin.getConfigManager();
        goalManager = plugin.getGoalManager();

        riders = new Hashtable<String, Rider>();

        if (Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, this, UPDATE_DELAY, Math.min(configManager.updatePeriod, MAX_UPDATE_PERIOD)) < 0) {
            Bukkit.getPluginManager().disablePlugin(plugin);
            MRLogger.getInstance().severe("Failed to schedule RiderManager task.");
        }
    }

    @Override
    public void run()
    {
        for (String playerName : riders.keySet()) {
            Rider rider = riders.get(playerName);
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
            ((CraftPlayer) player).getHandle().setPassengerOf(null);
            if (DependencyUtils.hasPermission(player, "mobrider.spawnegg") && hasSpawnEgg(target)) {
                SpawnEggTask spawnEggTask = new SpawnEggTask(target);
                spawnEggTask.setTaskId(Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, spawnEggTask, 0, 1));
            }
        }
    }

    private boolean hasSpawnEgg(Entity entity)
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
            rider.setHealth(Math.min(rider.getHealth() + 5, rider.getMaxHealth()));
            EntityUtils.removeItemInHand(rider.getPlayer());
            MessageUtils.sendMessage(rider, configManager.fedConfirmedMessage);
        }
    }

    public boolean canRide(Player player, Entity entity)
    {
        return isAllowed(player, entity) && ((player.isOp() || isOwner(player, entity) || (isWinner(player, entity)) && isWithdrawSuccess(player, entity)));
    }

    private boolean isAllowed(Player player, Entity entity)
    {
        if (player == null || entity == null) {
            return false;
        }

        if (entity.getPassenger() != null) {
            player.sendMessage("That creature already has a rider.");
            return false;
        }

        if (entity instanceof Animals || entity instanceof Squid || entity instanceof Golem || entity instanceof Villager) {
            if (DependencyUtils.hasPermission(player, "mobrider.animals") || DependencyUtils.hasPermission(player, "mobrider.animals." + EntityUtils.getCreatureName(entity).toLowerCase()))
                return true;
            else {
                player.sendMessage("You do not have permission to ride that animal.");
                return false;
            }
        }

        if (entity instanceof Monster || entity instanceof Ghast || entity instanceof Slime || entity instanceof EnderDragon) {
            if (DependencyUtils.hasPermission(player, "mobrider.monsters") || DependencyUtils.hasPermission(player, "mobrider.monsters." + EntityUtils.getCreatureName(entity).toLowerCase()))
                return true;
            else {
                player.sendMessage("You do not have permission to ride that monster.");
                return false;
            }
        }

        if (entity instanceof Player) {
            if (DependencyUtils.hasPermission(player, "mobrider.players") || DependencyUtils.hasPermission(player, "mobrider.players." + ((Player) entity).getName().toLowerCase()))
                return true;
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

    private boolean isWithdrawSuccess(Player player, Entity entity)
    {
        if (economy == null) {
            return true;
        }

        if (player == null || entity == null) {
            return false;
        }

        double cost = RideType.fromType(entity.getType()).getCost();

        if (cost == 0.0) {
            return true;
        }

        EconomyResponse response = economy.withdrawPlayer(player.getName(), cost);
        if (response.transactionSuccess()) {
            player.sendMessage("You were charged " + economy.format(response.amount) + " for riding this creature.");
        }
        else {
            player.sendMessage("You have insufficient funds to ride that creature.");
        }

        return response.transactionSuccess();
    }

    private static boolean isOwner(Player player, Entity entity)
    {
        if (entity instanceof Tameable) {
            if (((Tameable) entity).isTamed() && ((Tameable) entity).getOwner() instanceof Player) {
                Player owner = (Player) ((Tameable) entity).getOwner();
                if (owner.getName().equals(player.getName())) {
                    return true;
                }
            }
        }
        return false;
    }
}
