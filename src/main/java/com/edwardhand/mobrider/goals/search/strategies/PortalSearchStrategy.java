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

import org.bukkit.Location;

import com.edwardhand.mobrider.commons.DependencyUtils;
import com.edwardhand.mobrider.goals.LocationGoal;
import com.edwardhand.mobrider.rider.Rider;
import com.onarandombox.MultiverseCore.api.MVDestination;

public class PortalSearchStrategy extends LocationSearchStrategy
{
    @Override
    public boolean find(Rider rider, String portalName)
    {
        boolean foundPortal = false;

        if (DependencyUtils.hasMultiverse()) {
            MVDestination portalTest = DependencyUtils.getMVDestinationFactory().getDestination("p:" + portalName);

            if (portalTest.getType().equals("Portal")) {
                Location portalLocation = portalTest.getLocation(null);
                if (portalLocation.getWorld().equals(rider.getWorld())) {
                    rider.setGoal(new LocationGoal(portalLocation));
                    foundPortal = true;
                }
            }
        }

        return foundPortal;
    }
}
