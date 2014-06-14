/*
 * This file is part of MobRider.
 *
 * Copyright (c) 2011-2013, R. Ramos <http://github.com/mung3r/>
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

import java.util.EnumSet;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.util.BlockIterator;

public final class EntityUtils
{
    private static final EnumSet<Material> TRANSPARENT_BLOCKS = EnumSet.of(Material.AIR, Material.WATER);

    private static final EnumSet<EntityType> NEW_AI_MOBS = EnumSet.of(EntityType.CHICKEN, EntityType.COW, EntityType.CREEPER, EntityType.ENDER_DRAGON,
            EntityType.ENDERMAN, EntityType.HORSE, EntityType.IRON_GOLEM, EntityType.MUSHROOM_COW, EntityType.OCELOT, EntityType.PIG, EntityType.PIG_ZOMBIE,
            EntityType.SHEEP, EntityType.SKELETON, EntityType.SNOWMAN, EntityType.VILLAGER, EntityType.WITCH, EntityType.WITHER, EntityType.WITHER_SKULL,
            EntityType.WOLF, EntityType.ZOMBIE);

    private static final EnumSet<EntityType> AGGRESSIVE_MOBS = EnumSet.of(EntityType.BLAZE, EntityType.CAVE_SPIDER, EntityType.ENDER_DRAGON,
            EntityType.ENDERMAN, EntityType.GHAST, EntityType.GIANT, EntityType.IRON_GOLEM, EntityType.MAGMA_CUBE, EntityType.OCELOT, EntityType.PIG_ZOMBIE,
            EntityType.SILVERFISH, EntityType.SKELETON, EntityType.SLIME, EntityType.SNOWMAN, EntityType.SPIDER, EntityType.WITCH, EntityType.WITHER,
            EntityType.WITCH, EntityType.WOLF, EntityType.ZOMBIE);

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

    public static LivingEntity getNearByTarget(Player player, double range)
    {
        LivingEntity livingEntity = null;

        if (player != null) {
            for (Entity entity : player.getNearbyEntities(range, range, range)) {
                if (entity instanceof LivingEntity && isEntityWithinView(entity, player)) {
                    livingEntity = (LivingEntity) entity;
                    break;
                }
            }
        }

        return livingEntity;
    }

    private static boolean isEntityWithinView(Entity entity, Player player)
    {
        boolean withinView = true;

        double distance = player.getLocation().distance(entity.getLocation());
        BlockIterator blocks = new BlockIterator(player, (int) distance);

        while (blocks.hasNext()) {
            if (!TRANSPARENT_BLOCKS.contains(blocks.next().getType())) {
                withinView = false;
                break;
            }
        }

        return withinView;
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
