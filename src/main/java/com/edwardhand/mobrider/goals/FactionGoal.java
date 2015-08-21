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
package com.edwardhand.mobrider.goals;

import org.bukkit.Location;

import com.edwardhand.mobrider.rider.Rider;
import com.massivecraft.factions.entity.BoardColl;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.massivecore.ps.PS;

public class FactionGoal extends LocationGoal
{
    private Faction faction;

    public FactionGoal(Faction faction)
    {
        super(getDestination(faction));
        this.faction = faction;
    }

    @Override
    protected boolean isCloseToLocation(Rider rider, double range)
    {
        return isWithinRange(rider.getRide().getLocation(), getDestination(), range) || isWithinFaction(rider.getRide().getLocation());
    }

    private boolean isWithinFaction(Location currentLocation)
    {
        boolean isWithinFaction = false;

        Faction testFaction = BoardColl.get().getFactionAt(PS.valueOf(currentLocation));
        if (testFaction.getId().equals(faction.getId())) {
            isWithinFaction = true;
        }

        return isWithinFaction;
    }

    private static Location getDestination(Faction faction)
    {
        return faction.getHome().asBukkitLocation();
    }
}
