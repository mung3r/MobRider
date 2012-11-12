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

import java.util.Iterator;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;

import com.edwardhand.mobrider.commons.DependencyUtils;
import com.edwardhand.mobrider.rider.Rider;
import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class RegionGoal extends LocationGoal
{
    private ProtectedRegion region;
    private RegionManager regionManager;

    public RegionGoal(ProtectedRegion region, World world)
    {
        super(getDestination(region, world));
        this.region = region;
        regionManager = DependencyUtils.getRegionManager(world);
    }

    @Override
    public void update(Rider rider, double range)
    {
        if (rider != null) {
            rider.setTarget(null);
            LivingEntity ride = rider.getRide();

            if (ride != null) {
                if (isWithinRange(ride.getLocation(), destination, range) || isWithinRegion(ride.getLocation())) {
                    setGoalDone(true);
                }
                else {
                    setPathEntity(rider, destination);
                    updateSpeed(rider);
                }
            }
        }
    }

    private boolean isWithinRegion(Location currentLocation)
    {
        boolean isWithinRegion = false;

        Iterator<ProtectedRegion> regionIterator = regionManager.getApplicableRegions(currentLocation).iterator();
        while (regionIterator.hasNext()) {
            if (regionIterator.next().getId().equals(region.getId())) {
                isWithinRegion = true;
                break;
            }
        }

        return isWithinRegion;
    }

    private static Location getDestination(ProtectedRegion region, World world)
    {
        Location midPoint = null;

        if (region != null && world != null) {

            BlockVector minPoint = region.getMinimumPoint();
            BlockVector maxPoint = region.getMaximumPoint();

            double x = (minPoint.getX() + maxPoint.getX()) / 2;
            double z = (minPoint.getZ() + maxPoint.getZ()) / 2;

            midPoint = world.getHighestBlockAt((int) x, (int) z).getLocation();
        }

        return midPoint;
    }
}
