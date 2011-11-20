package com.edwardhand.mobrider.utils;

import org.bukkit.Material;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Blaze;
import org.bukkit.entity.CaveSpider;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.Cow;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Ghast;
import org.bukkit.entity.Giant;
import org.bukkit.entity.Monster;
import org.bukkit.entity.MushroomCow;
import org.bukkit.entity.Pig;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.entity.Silverfish;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Slime;
import org.bukkit.entity.Snowman;
import org.bukkit.entity.Spider;
import org.bukkit.entity.Squid;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Wolf;
import org.bukkit.entity.Zombie;

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
        }

        return false;
    }

    public static CreatureType getCreatureType(Entity entity)
    {
        if (entity instanceof Blaze)
            return CreatureType.BLAZE;
        if (entity instanceof CaveSpider)
            return CreatureType.CAVE_SPIDER;
        if (entity instanceof Chicken)
            return CreatureType.CHICKEN;
        if (entity instanceof Cow)
            return CreatureType.COW;
        if (entity instanceof Creeper)
            return CreatureType.CREEPER;
        if (entity instanceof Enderman)
            return CreatureType.ENDERMAN;
        if (entity instanceof EnderDragon)
            return CreatureType.ENDER_DRAGON;
        if (entity instanceof Ghast)
            return CreatureType.GHAST;
        if (entity instanceof Giant)
            return CreatureType.GIANT;
        if (entity instanceof MushroomCow)
            return CreatureType.MUSHROOM_COW;
        if (entity instanceof Pig)
            return CreatureType.PIG;
        if (entity instanceof PigZombie)
            return CreatureType.PIG_ZOMBIE;
        if (entity instanceof Sheep)
            return CreatureType.SHEEP;
        if (entity instanceof Skeleton)
            return CreatureType.SKELETON;
        if (entity instanceof Slime)
            return CreatureType.SLIME;
        if (entity instanceof Silverfish)
            return CreatureType.SILVERFISH;
        if (entity instanceof Spider)
            return CreatureType.SPIDER;
        if (entity instanceof Squid)
            return CreatureType.SQUID;
        if (entity instanceof Zombie)
            return CreatureType.ZOMBIE;
        if (entity instanceof Villager)
            return CreatureType.VILLAGER;
        if (entity instanceof Wolf)
            return CreatureType.WOLF;

        // Monster is a parent class and needs to be last
        if (entity instanceof Monster)
            return CreatureType.MONSTER;
        return null;
    }

    public static String getCreatureName(Entity entity)
    {
        CreatureType type = getCreatureType(entity);

        return type == null ? "" : type.name();
    }

    public static boolean isFood(Material mat)
    {
        switch (mat) {
            case BROWN_MUSHROOM:
            case CACTUS:
            case CAKE:
            case EGG:
            case ICE:
            case INK_SACK:
            case LEATHER:
            case RED_MUSHROOM:
            case RED_ROSE:
            case YELLOW_FLOWER:
            case SAPLING:
            case SEEDS:
            case SLIME_BALL:
            case WHEAT:
            case SUGAR:
            case SUGAR_CANE:
                return true;
        }
        return false;
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

        if (entity instanceof Animals || entity instanceof Squid || entity instanceof Snowman) {
            if (MobRider.permission.playerHas(player, "mobRider.animals") || MobRider.permission.playerHas(player, "mobRider.animals." + MRUtil.getCreatureName(entity).toLowerCase()))
                return true;
            else {
                player.sendMessage("You do not have permission to ride animals.");
                return false;
            }
        }

        if (entity instanceof Monster || entity instanceof EnderDragon) {
            if (MobRider.permission.playerHas(player, "mobRider.monsters") || MobRider.permission.playerHas(player, "mobRider.monsters." + MRUtil.getCreatureName(entity).toLowerCase()))
                return true;
            else {
                player.sendMessage("You do not have permission to ride monsters.");
                return false;
            }
        }

        if (entity instanceof Player || entity instanceof Villager) {
            if (MobRider.permission.playerHas(player, "mobRider.players") || MobRider.permission.playerHas(player, "mobRider.players." + ((Player) entity).getName().toLowerCase()))
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
