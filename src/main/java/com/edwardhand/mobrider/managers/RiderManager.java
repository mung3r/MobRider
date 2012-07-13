package com.edwardhand.mobrider.managers;

import java.util.Hashtable;
import java.util.Map;
import java.util.Random;

import net.milkbowl.vault.permission.Permission;

import org.bukkit.Bukkit;
import org.bukkit.entity.Animals;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Ghast;
import org.bukkit.entity.Golem;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.entity.Slime;
import org.bukkit.entity.Squid;
import org.bukkit.entity.Villager;

import com.edwardhand.mobrider.MobRider;
import com.edwardhand.mobrider.models.RideType;
import com.edwardhand.mobrider.models.Rider;
import com.edwardhand.mobrider.utils.MRUtil;

public class RiderManager implements Runnable
{
    private static final long UPDATE_DELAY = 5L;
    private static final long UPDATE_PERIOD = 1L;
    private Random random = new Random();

    private Permission permission;
    private MetricsManager metrics;
    private ConfigManager configManager;
    private GoalManager goalManager;
    private MessageManager messageManager;
    private Map<String, Rider> riders;

    public RiderManager(MobRider plugin)
    {
        permission = plugin.getPermission();
        metrics = plugin.getMetricsManager();
        configManager = plugin.getConfigManager();
        goalManager = plugin.getGoalManager();
        messageManager = plugin.getMessageManager();

        riders = new Hashtable<String, Rider>();

        if (Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, this, UPDATE_DELAY, UPDATE_PERIOD) < 0) {
            Bukkit.getPluginManager().disablePlugin(plugin);
            MobRider.getMRLogger().severe("Failed to schedule task.");
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
            messageManager.sendMessage(rider, configManager.fedConfusedMessage);
        }
        else {
            rider.setHealth(Math.min(rider.getHealth() + 5, rider.getMaxHealth()));
            MRUtil.removeItemInHand(rider.getPlayer());
            messageManager.sendMessage(rider, configManager.fedConfirmedMessage);
        }
    }

    public boolean canRide(Player player, Entity entity)
    {
        if (player == null || entity == null) {
            return false;
        }

        if (entity.getPassenger() != null) {
            player.sendMessage("That creature already has a rider.");
            return false;
        }

        if (permission == null) {
            return isWinner(player, entity);
        }

        if (entity instanceof Animals || entity instanceof Squid || entity instanceof Golem || entity instanceof Villager) {
            if (permission.playerHas(player, "mobrider.animals") || permission.playerHas(player, "mobrider.animals." + MRUtil.getCreatureName(entity).toLowerCase()))
                return isWinner(player, entity);
            else {
                player.sendMessage("You do not have permission to ride that animal.");
                return false;
            }
        }

        if (entity instanceof Monster || entity instanceof Ghast || entity instanceof Slime || entity instanceof EnderDragon) {
            if (permission.playerHas(player, "mobrider.monsters") || permission.playerHas(player, "mobrider.monsters." + MRUtil.getCreatureName(entity).toLowerCase()))
                return isWinner(player, entity);
            else {
                player.sendMessage("You do not have permission to ride that monster.");
                return false;
            }
        }

        if (entity instanceof Player) {
            if (permission.playerHas(player, "mobrider.players") || permission.playerHas(player, "mobrider.players." + ((Player) entity).getName().toLowerCase()))
                return true;
            else {
                player.sendMessage("You do not have permission to ride that player.");
                return false;
            }
        }

        return false;
    }

    private boolean isWinner(Player player, Entity entity)
    {
        boolean isWinner = RideType.fromType(entity.getType()) != null ? random.nextDouble() * 100D < RideType.fromType(entity.getType()).getChance() : true;

        if (!isWinner && player != null) {
            player.sendMessage("You have bad luck trying to ride that creature.");
        }

        return isWinner;
    }
}
