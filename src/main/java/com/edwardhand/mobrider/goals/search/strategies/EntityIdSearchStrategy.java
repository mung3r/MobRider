package com.edwardhand.mobrider.goals.search.strategies;

import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import com.edwardhand.mobrider.commons.EntityUtils;
import com.edwardhand.mobrider.rider.Rider;

public class EntityIdSearchStrategy extends LivingEntitySearchStrategy
{
    @Override
    public LivingEntity find(Rider rider, String searchTerm, double searchRange)
    {
        Player player = rider.getPlayer();
        LivingEntity foundEntity = null;

        if (EntityUtils.isInteger(searchTerm)) {
            net.minecraft.server.Entity entity = ((CraftWorld) player.getWorld()).getHandle().getEntity(Integer.valueOf(searchTerm));
            if (entity instanceof LivingEntity) {
                if (isEntityWithinRange((LivingEntity) entity, player, searchRange)) {
                    foundEntity = (LivingEntity) entity;
                }
            }
        }

        return foundEntity;
    }
}
