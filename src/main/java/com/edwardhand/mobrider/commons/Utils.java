package com.edwardhand.mobrider.commons;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.server.AxisAlignedBB;
import net.minecraft.server.EntityLiving;
import net.minecraft.server.EntityPlayer;

import org.bukkit.Location;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class Utils
{
    public static boolean isInteger(final String s)
    {
        try {
            Integer.parseInt(s);
        }
        catch (NumberFormatException e) {
            return false;
        }

        return true;
    }

    public static String getCreatureName(Entity entity)
    {
        return entity == null || entity.getType().name() == null ? "" : entity.getType().name();
    }

    public static LivingEntity getNearByTarget(Player player, int range)
    {
        LivingEntity livingEntity = null;

        if (player != null) {
            EntityPlayer mcPlayer = ((CraftPlayer) player).getHandle();

            Location loc = player.getTargetBlock(null, range).getLocation();
            CraftWorld craftWorld = (CraftWorld) player.getWorld();
            double x1 = loc.getX() + 0.5D;
            double y1 = loc.getY() + 0.5D;
            double z1 = loc.getZ() + 0.5D;

            @SuppressWarnings("rawtypes")
            List entities = new ArrayList();
            double r = 0.5D;
            while ((entities.size() == 0) && (r < range)) {
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

    public static boolean hasNewAI(LivingEntity livingEntity)
    {
        return livingEntity != null && (livingEntity.getType() == EntityType.CHICKEN
          || livingEntity.getType() == EntityType.COW
          || livingEntity.getType() == EntityType.CREEPER
          || livingEntity.getType() == EntityType.IRON_GOLEM
          || livingEntity.getType() == EntityType.MUSHROOM_COW
          || livingEntity.getType() == EntityType.OCELOT
          || livingEntity.getType() == EntityType.PIG
          || livingEntity.getType() == EntityType.SHEEP
          || livingEntity.getType() == EntityType.SKELETON
          || livingEntity.getType() == EntityType.SNOWMAN
          || livingEntity.getType() == EntityType.VILLAGER
          || livingEntity.getType() == EntityType.WOLF
          || livingEntity.getType() == EntityType.ZOMBIE);
    }

    public static boolean isAggressive(LivingEntity livingEntity)
    {
        return livingEntity != null && (livingEntity.getType() == EntityType.CREEPER
          || livingEntity.getType() == EntityType.IRON_GOLEM
          || livingEntity.getType() == EntityType.OCELOT
          || livingEntity.getType() == EntityType.SKELETON
          || livingEntity.getType() == EntityType.SNOWMAN
          || livingEntity.getType() == EntityType.WOLF
          || livingEntity.getType() == EntityType.ZOMBIE);
    }
}
