package com.edwardhand.mobrider.goals.search;

import org.bukkit.entity.LivingEntity;

import com.edwardhand.mobrider.rider.Rider;

public interface LivingEntitySearch
{
    public LivingEntity find(Rider rider, String searchTerm, double searchRange);
}
