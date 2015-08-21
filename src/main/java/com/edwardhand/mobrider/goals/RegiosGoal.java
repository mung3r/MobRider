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

import net.jzx7.regios.regions.RegiosCuboidRegion;
import net.jzx7.regios.regions.RegiosPolyRegion;
import net.jzx7.regiosapi.location.RegiosPoint;
import net.jzx7.regiosapi.regions.Region;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.edwardhand.mobrider.commons.DependencyUtils;
import com.edwardhand.mobrider.rider.Rider;

public class RegiosGoal extends LocationGoal
{
    private Region region;

    public RegiosGoal(Region region, World world)
    {
        super(getDestination(region, world));
        this.region = region;
    }

    @Override
    protected boolean isCloseToLocation(Rider rider, double range)
    {
        return isWithinRange(rider.getRide().getLocation(), getDestination(), range) || isWithinRegion(rider.getPlayer());
    }

    private boolean isWithinRegion(Player player)
    {
        return region.equals(DependencyUtils.getRegiosAPI().getRegion(player));
    }

    private static Location getDestination(Region region, World world)
    {
        Location midPoint = null;

        if (region != null && world != null) {

            double x = 0;
            double z = 0;

            if (region instanceof RegiosCuboidRegion) {
                RegiosCuboidRegion cuboidRegion = (RegiosCuboidRegion) region;
                RegiosPoint minPoint = cuboidRegion.getL1();
                RegiosPoint maxPoint = cuboidRegion.getL2();

                x = (minPoint.getX() + maxPoint.getX()) / 2;
                z = (minPoint.getZ() + maxPoint.getZ()) / 2;
            }
            else if (region instanceof RegiosPolyRegion) {
                RegiosPolyRegion polyRegion = (RegiosPolyRegion) region;

                x = polyRegion.get2DPolygon().getBounds2D().getCenterX();
                z = polyRegion.get2DPolygon().getBounds2D().getCenterY();
            }

            midPoint = world.getHighestBlockAt((int) x, (int) z).getLocation();
        }

        return midPoint;
    }
}
