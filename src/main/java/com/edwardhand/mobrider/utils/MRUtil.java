package com.edwardhand.mobrider.utils;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.server.AxisAlignedBB;
import net.minecraft.server.EntityLiving;
import net.minecraft.server.EntityPlayer;

import org.bukkit.Location;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftPlayer;
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
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

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
            // do nothing, really
        }

        return false;
    }

    public static String getCreatureName(Entity entity)
    {
        return entity.getType().name() == null ? "" : entity.getType().name();
    }

    public static boolean canRide(Player player, Entity entity)
    {
        if (player == null || entity == null) {
            return false;
        }

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

    public static LivingEntity getTargetLivingEntity(Player player)
    {
        LivingEntity livingEntity = null;

        if (player != null) {
            EntityPlayer mcPlayer = ((CraftPlayer) player).getHandle();

            Location loc = player.getTargetBlock(null, (int) MRConfig.MOUNT_RANGE).getLocation();
            CraftWorld craftWorld = (CraftWorld) player.getWorld();
            double x1 = loc.getX() + 0.5D;
            double y1 = loc.getY() + 0.5D;
            double z1 = loc.getZ() + 0.5D;

            @SuppressWarnings("rawtypes")
            List entities = new ArrayList();
            double r = 0.5D;
            while ((entities.size() == 0) && (r < MRConfig.MOUNT_RANGE)) {
                AxisAlignedBB bb = AxisAlignedBB.a(x1 - r, y1 - r, z1 - r, x1 + r, y1 + r, z1 + r);
                entities = craftWorld.getHandle().getEntities(mcPlayer, bb);
                r += 0.5D;
            }

            if ((entities.size() == 1) && ((entities.get(0) instanceof EntityLiving))) {
                EntityLiving entity = (EntityLiving) entities.get(0);
                livingEntity = (LivingEntity) (entity.getBukkitEntity());
            }
        }

        return livingEntity;
    }

    public static void removeItemInHand(Player player)
    {
        if (player != null) {
            PlayerInventory inv = player.getInventory();
            ItemStack stack = player.getItemInHand();

            if (stack.getAmount() > 1) {
                stack.setAmount(stack.getAmount() - 1);
                inv.setItem(inv.getHeldItemSlot(), stack);
            }
            else {
                inv.setItem(inv.getHeldItemSlot(), null);
            }
        }
    }
}
