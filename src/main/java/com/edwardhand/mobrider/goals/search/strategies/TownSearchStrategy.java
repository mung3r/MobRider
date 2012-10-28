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
import com.edwardhand.mobrider.goals.TownyGoal;
import com.edwardhand.mobrider.rider.Rider;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.TownyUniverse;

public class TownSearchStrategy extends LocationSearchStrategy
{
    @Override
    public boolean find(Rider rider, String townName)
    {
        boolean foundTown = false;

        if (DependencyUtils.hasTowny()) {
            for (Town town : TownyUniverse.getDataSource().getTowns()) {
                if (town.getName().equals(townName)) {
                    rider.setGoal(new TownyGoal(town));
                    foundTown = true;
                }
            }
        }

        return foundTown;
    }
}
