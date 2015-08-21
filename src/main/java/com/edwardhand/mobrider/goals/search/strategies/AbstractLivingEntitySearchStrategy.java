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
package com.edwardhand.mobrider.goals.search.strategies;

import org.bukkit.entity.LivingEntity;

import com.edwardhand.mobrider.goals.search.LivingEntitySearch;
import com.edwardhand.mobrider.rider.Rider;

public abstract class AbstractLivingEntitySearchStrategy implements LivingEntitySearch
{
    @Override
    public abstract LivingEntity find(Rider rider, String searchTerm, double searchRange);

    protected boolean isEntityWithinRange(LivingEntity from, LivingEntity to, double range)
    {
        return from != null && to != null && !from.equals(to) && from.getWorld().equals(to.getWorld())
                && from.getLocation().distanceSquared(to.getLocation()) < range * range;
    }
}
