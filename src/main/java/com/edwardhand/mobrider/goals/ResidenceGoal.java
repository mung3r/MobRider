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

import com.bekvon.bukkit.residence.protection.ClaimedResidence;
import com.bekvon.bukkit.residence.protection.CuboidArea;
import com.edwardhand.mobrider.rider.Rider;

public class ResidenceGoal extends LocationGoal
{
    private ClaimedResidence residence;

    public ResidenceGoal(ClaimedResidence residence)
    {
        super(getDestination(residence));
        this.residence = residence;
    }

    @Override
    protected boolean isCloseToLocation(Rider rider, double range)
    {
        return isWithinRange(rider.getRide().getLocation(), getDestination(), range) || isWithinResidence(rider.getRide().getLocation());
    }

    private boolean isWithinResidence(Location location)
    {
        return residence.containsLoc(location);
    }

    private static Location getDestination(ClaimedResidence residence)
    {
        Location midPoint = null;

        if (residence.getAreaArray().length > 0) {
            midPoint = getMidPoint(residence.getAreaArray()[0]);
        }

        return midPoint;
    }

    private static Location getMidPoint(CuboidArea area)
    {
        Location midPoint = null;

        if (area != null) {
            Location lowLoc = area.getLowLoc();
            Location highLoc = area.getHighLoc();

            double x = (lowLoc.getX() + highLoc.getX()) / 2;
            double z = (lowLoc.getZ() + highLoc.getZ()) / 2;

            midPoint = area.getWorld().getHighestBlockAt((int) x, (int) z).getLocation();
        }

        return midPoint;
    }
}
