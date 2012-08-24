package com.edwardhand.mobrider.goals.search.strategies;

import net.citizensnpcs.api.CitizensManager;
import net.citizensnpcs.resources.npclib.HumanNPC;
import net.citizensnpcs.resources.npclib.NPCList;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import com.edwardhand.mobrider.commons.DependencyUtils;
import com.edwardhand.mobrider.rider.Rider;

public class CitizenSearchStrategy extends LivingEntitySearchStrategy
{
    @Override
    public LivingEntity find(Rider rider, String searchTerm, double searchRange)
    {
        Player player = rider.getPlayer();
        LivingEntity foundEntity = null;

        if (DependencyUtils.hasCitizens()) {
            NPCList npcList = CitizensManager.getList();
            for (HumanNPC npc : npcList.values()) {
                if (npc.getName().equalsIgnoreCase(searchTerm)) {
                    if (isEntityWithinRange(player, npc.getPlayer(), searchRange)) {
                        foundEntity = npc.getPlayer();
                        break;
                    }
                }
            }
        }

        return foundEntity;
    }
}
