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
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;

import com.edwardhand.mobrider.rider.Rider;

public class LocationGoal extends AbstractGoal
{
    protected Location destination;

    public LocationGoal(Location destination)
    {
        this.destination = destination;
    }

    public LocationGoal(LivingEntity livingEntity)
    {
        this(livingEntity.getLocation());
    }

    @Override
    public void update(Rider rider, double range)
    {
        if (rider != null) {
            rider.setTarget(null);
            LivingEntity ride = rider.getRide();

            if (ride != null) {
                if (isWithinRange(ride.getLocation(), destination, range)) {
                    setGoalDone(true);
                }
                else {
                    setPathEntity(rider, destination);
                    updateSpeed(rider);
                }
            }
        }
    }

    protected static boolean isWithinRange(Location start, Location end, double distance)
    {
        return !start.getWorld().equals(end.getWorld()) || start.distanceSquared(end) <= (distance * distance);
    }

    protected static void updateSpeed(Rider rider)
    {
        if (rider != null) {
            LivingEntity ride = rider.getRide();

            if (ride != null && rider.getRideType() != null) {
                Vector velocity = ride.getVelocity();
                double saveY = velocity.getY();
                velocity.normalize().multiply(rider.getSpeed());
                velocity.setY(saveY);
                ride.setVelocity(velocity);
            }
        }
    }
}
