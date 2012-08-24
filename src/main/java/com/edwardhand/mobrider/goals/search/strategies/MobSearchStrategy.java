package com.edwardhand.mobrider.goals.search.strategies;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import com.edwardhand.mobrider.rider.Rider;

public class MobSearchStrategy extends LivingEntitySearchStrategy
{
    @Override
    public LivingEntity find(Rider rider, String searchTerm, double searchRange)
    {
        Player player = rider.getPlayer();
        LivingEntity foundEntity = null;

        LivingEntity ride = rider.getRide();
        if (ride != null) {
            double lastDistance = Double.MAX_VALUE;

            for (Entity entity : player.getNearbyEntities(2 * searchRange, 2 * searchRange, 2 * searchRange)) {

                if (entity instanceof LivingEntity) {
                    EntityType creatureType = entity.getType();

                    if (creatureType != null && creatureType.name().equalsIgnoreCase(searchTerm)) {
                        double entityDistance = player.getLocation().distanceSquared(entity.getLocation());

                        if (lastDistance > entityDistance && entity.getEntityId() != player.getEntityId() && entity.getEntityId() != ride.getEntityId()) {
                            lastDistance = entityDistance;
                            foundEntity = (LivingEntity) entity;
                        }
                    }
                }
            }
        }

        return foundEntity;
    }
}
