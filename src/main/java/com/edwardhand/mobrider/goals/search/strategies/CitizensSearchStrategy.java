/*
 * This file is part of MobRider.
 *
 * Copyright (c) 2011-2015, R. Ramos <http://github.com/mung3r/>
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
package com.edwardhand.mobrider.goals.search.strategies;

import net.citizensnpcs.api.CitizensManager;
import net.citizensnpcs.resources.npclib.HumanNPC;
import net.citizensnpcs.resources.npclib.NPCList;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import com.edwardhand.mobrider.commons.DependencyUtils;
import com.edwardhand.mobrider.rider.Rider;

public class CitizensSearchStrategy extends AbstractLivingEntitySearchStrategy
{
    @Override
    public LivingEntity find(Rider rider, String searchTerm, double searchRange)
    {
        Player player = rider.getPlayer();
        LivingEntity foundEntity = null;

        if (DependencyUtils.hasCitizens()) {
            NPCList npcList = CitizensManager.getList();
            for (HumanNPC npc : npcList.values()) {
                if (npc.getName().equalsIgnoreCase(searchTerm) && isEntityWithinRange(player, npc.getPlayer(), searchRange)) {
                    foundEntity = npc.getPlayer();
                    break;
                }
            }
        }

        return foundEntity;
    }
}
