/*
 * This file is part of MobRider.
 *
 * Copyright (c) 2011-2012, R. Ramos <http://github.com/mung3r/>
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

import com.edwardhand.mobrider.commons.LoggerUtil;
import com.edwardhand.mobrider.rider.Rider;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.exceptions.TownyException;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.TownBlock;
import com.palmergames.bukkit.towny.object.TownyUniverse;

public class TownyGoal extends LocationGoal
{
    private Town town;

    public TownyGoal(Town town)
    {
        super(getDestination(town));
        this.town = town;
    }

    @Override
    protected boolean isCloseToLocation(Rider rider, double range)
    {
        return isWithinRange(rider.getRide().getLocation(), getDestination(), range) || isWithinTown(rider.getRide().getLocation());
    }

    private boolean isWithinTown(Location currentLocation)
    {
        boolean isWithinTown = false;

        try {
            TownBlock block = TownyUniverse.getTownBlock(currentLocation);
            if (block != null && block.getTown().getName().equals(town.getName())) {
                isWithinTown = true;
            }
        }
        catch (NotRegisteredException e) {
            LoggerUtil.getInstance().warning("Town not registered");
        }

        return isWithinTown;
    }

    private static Location getDestination(Town town)
    {
        Location spawn = null;

        try {
            spawn = town.getSpawn();
        }
        catch (TownyException e) {
            LoggerUtil.getInstance().warning("Town spawn not found");
        }
        return spawn;
    }
}
