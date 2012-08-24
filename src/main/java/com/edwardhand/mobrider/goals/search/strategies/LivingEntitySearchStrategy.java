package com.edwardhand.mobrider.goals.search.strategies;

import org.bukkit.entity.LivingEntity;

import com.edwardhand.mobrider.goals.search.LivingEntitySearch;
import com.edwardhand.mobrider.rider.Rider;

public class LivingEntitySearchStrategy implements LivingEntitySearch
{
    @Override
    public LivingEntity find(Rider rider, String searchTerm, double searchRange)
    {
        return null;
    }

    protected boolean isEntityWithinRange(LivingEntity from, LivingEntity to, double range)
    {
        return from != null && to != null && !from.equals(to) && from.getWorld().equals(to.getWorld())
                && from.getLocation().distanceSquared(to.getLocation()) < range * range;
    }
}
