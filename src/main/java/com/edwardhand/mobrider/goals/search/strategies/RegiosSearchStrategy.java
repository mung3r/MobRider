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
package com.edwardhand.mobrider.goals.search.strategies;

import com.edwardhand.mobrider.commons.DependencyUtils;
import com.edwardhand.mobrider.goals.RegiosGoal;
import com.edwardhand.mobrider.rider.Rider;

import couk.Adamki11s.Regios.Regions.Region;

public class RegiosSearchStrategy extends AbstractLocationSearchStrategy
{
    @Override
    public boolean find(Rider rider, String regionName)
    {
        boolean foundRegion = false;

        if (DependencyUtils.hasRegios()) {
            Region region = DependencyUtils.getRegiosAPI().getRegion(regionName);
            if (region != null && region.getWorld().equals(rider.getWorld())) {
                rider.setGoal(new RegiosGoal(region, rider.getWorld()));
                foundRegion = true;
            }
        }

        return foundRegion;
    }
}
