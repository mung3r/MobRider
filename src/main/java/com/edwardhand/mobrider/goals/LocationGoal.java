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
package com.edwardhand.mobrider.goals;

import org.bukkit.Location;

import com.edwardhand.mobrider.rider.Rider;

public class LocationGoal extends AbstractGoal
{
    private Location destination;

    public LocationGoal(Location destination)
    {
        this.destination = destination;
    }

    public Location getDestination()
    {
        return destination;
    }

    public void setDestination(Location destination)
    {
        this.destination = destination;
    }

    @Override
    public void update(Rider rider, double range)
    {
        if (rider != null) {
            rider.setTarget(null);

            if (rider.getRide() != null) {
                if (isCloseToLocation(rider, range)) {
                    handleLocation(rider);
                }
                else {
                    doTravel(rider);
                }
            }
        }
    }

    protected boolean isCloseToLocation(Rider rider, double range)
    {
        return isWithinRange(rider.getRide().getLocation(), destination, range);
    }

    protected void handleLocation(Rider rider)
    {
        setGoalDone(true);
    }

    protected void doTravel(Rider rider)
    {
        setPathEntity(rider, destination);
        updateSpeed(rider);
    }
}
