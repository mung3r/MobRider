package com.edwardhand.mobrider.goals.search.strategies;

import java.util.Iterator;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import com.edwardhand.mobrider.commons.DependencyUtils;
import com.edwardhand.mobrider.rider.Rider;

public class Citizens2SearchStrategy extends LivingEntitySearchStrategy
{
    @Override
    public LivingEntity find(Rider rider, String searchTerm, double searchRange)
    {
        Player player = rider.getPlayer();
        LivingEntity foundEntity = null;

        if (DependencyUtils.hasCitizens2()) {
            Iterator<NPC> npcIterator = CitizensAPI.getNPCRegistry().iterator();
            while (npcIterator.hasNext()) {
                NPC npc = npcIterator.next();
                if (npc.getName().equalsIgnoreCase(searchTerm)) {
                    if (isEntityWithinRange(player, npc.getBukkitEntity(), searchRange)) {
                        foundEntity = npc.getBukkitEntity();
                    }
                }
            }
        }

        return foundEntity;
    }
}
