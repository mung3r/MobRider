package com.edwardhand.mobrider.utils;

import org.bukkit.entity.Animals;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Ghast;
import org.bukkit.entity.Golem;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.entity.Slime;
import org.bukkit.entity.Squid;
import org.bukkit.entity.Villager;

import com.edwardhand.mobrider.MobRider;

public class MRUtil
{
    public static boolean isNumber(final String s)
    {
        try {
            Integer.parseInt(s);
            return true;
        }
        catch (NumberFormatException e) {
            MobRider.getMRLogger().debug(e.getMessage());
        }

        return false;
    }

    public static String getCreatureName(Entity entity)
    {
        return entity.getType().name() == null ? "" : entity.getType().name();
    }

    public static boolean canRide(Player player, Entity entity)
    {
        if (entity.getPassenger() != null) {
            player.sendMessage("That creature already has a rider.");
            return false;
        }

        if (MobRider.permission == null) {
            return true;
        }

        if (entity instanceof Animals || entity instanceof Squid || entity instanceof Golem || entity instanceof Villager) {
            if (MobRider.permission.playerHas(player, "mobrider.animals") || MobRider.permission.playerHas(player, "mobrider.animals." + MRUtil.getCreatureName(entity).toLowerCase()))
                return true;
            else {
                player.sendMessage("You do not have permission to ride animals.");
                return false;
            }
        }

        if (entity instanceof Monster || entity instanceof EnderDragon) {
            if (MobRider.permission.playerHas(player, "mobrider.monsters") || MobRider.permission.playerHas(player, "mobrider.monsters." + MRUtil.getCreatureName(entity).toLowerCase()))
                return true;
            else {
                player.sendMessage("You do not have permission to ride monsters.");
                return false;
            }
        }

        if (entity instanceof Player) {
            if (MobRider.permission.playerHas(player, "mobrider.players") || MobRider.permission.playerHas(player, "mobrider.players." + ((Player) entity).getName().toLowerCase()))
                return true;
            else {
                player.sendMessage("You do not have permission to ride players.");
                return false;
            }
        }

        if (entity instanceof Ghast || entity instanceof Slime) {
            // Silently fail; no support for these two yet.
            return false;
        }

        return false;
    }
}
