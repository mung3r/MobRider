package com.edwardhand.mobrider.goals.search.strategies;

import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import com.edwardhand.mobrider.rider.Rider;

public class PlayerSearchStrategy extends LivingEntitySearchStrategy
{
    @Override
    public LivingEntity find(Rider rider, String searchTerm, double searchRange)
    {
        Player player = rider.getPlayer();
        LivingEntity foundEntity = null;

        Player foundPlayer = Bukkit.getPlayer(searchTerm);
        if (isEntityWithinRange(foundPlayer, player, searchRange)) {
            foundEntity = foundPlayer;
        }

        return foundEntity;
    }
}
