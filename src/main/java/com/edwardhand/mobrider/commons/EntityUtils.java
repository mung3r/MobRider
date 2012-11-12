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
package com.edwardhand.mobrider.commons;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import net.minecraft.server.AxisAlignedBB;
import net.minecraft.server.EntityLiving;
import net.minecraft.server.EntityPlayer;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public final class EntityUtils
{
    private static double ONE_HALF = 0.5D;

    private static final EnumSet<Material> TRANSPARENT_BLOCKS = EnumSet.of(Material.AIR, Material.WATER);

    private static final EnumSet<EntityType> NEW_AI_MOBS = EnumSet.of(EntityType.CHICKEN, EntityType.COW, EntityType.CREEPER, EntityType.IRON_GOLEM,
            EntityType.MUSHROOM_COW, EntityType.OCELOT, EntityType.PIG, EntityType.SHEEP, EntityType.SKELETON, EntityType.SNOWMAN, EntityType.VILLAGER,
            EntityType.WITCH, EntityType.WOLF, EntityType.ZOMBIE);

    private static final EnumSet<EntityType> AGGRESSIVE_MOBS = EnumSet.of(EntityType.BLAZE, EntityType.CAVE_SPIDER, EntityType.ENDER_DRAGON,
            EntityType.ENDERMAN, EntityType.GHAST, EntityType.GIANT, EntityType.IRON_GOLEM, EntityType.MAGMA_CUBE, EntityType.OCELOT, EntityType.PIG_ZOMBIE,
            EntityType.SILVERFISH, EntityType.SKELETON, EntityType.SLIME, EntityType.SNOWMAN, EntityType.SPIDER, EntityType.WITCH, EntityType.WITHER,
            EntityType.WOLF, EntityType.ZOMBIE);

    private EntityUtils()
    {
    }

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
            double x1 = loc.getX() + ONE_HALF;
            double y1 = loc.getY() + ONE_HALF;
            double z1 = loc.getZ() + ONE_HALF;

            @SuppressWarnings("rawtypes")
            List entities = new ArrayList();
            double r = ONE_HALF;
            while ((entities.size() == 0) && (r < range)) {
                AxisAlignedBB bb = AxisAlignedBB.a(x1 - r, y1 - r, z1 - r, x1 + r, y1 + r, z1 + r);
                entities = craftWorld.getHandle().getEntities(mcPlayer, bb);
                r += ONE_HALF;
            }

            if ((entities.size() == 1) && ((entities.get(0) instanceof EntityLiving))) {
                EntityLiving entity = (EntityLiving) entities.get(0);
                livingEntity = (LivingEntity) (entity.getBukkitEntity());

                List<Block> blocks = player.getLineOfSight(null, (int) player.getLocation().distance(livingEntity.getLocation()));
                for (Block block : blocks) {
                    if (!TRANSPARENT_BLOCKS.contains(block.getType())) {
                        livingEntity = null;
                    }
                }
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
        return livingEntity != null && NEW_AI_MOBS.contains(livingEntity.getType());
    }

    public static boolean isAggressive(LivingEntity livingEntity)
    {
        return livingEntity != null && AGGRESSIVE_MOBS.contains(livingEntity.getType());
    }
}
