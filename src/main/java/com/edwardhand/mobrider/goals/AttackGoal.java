/*
 * This file is part of MobRider.
 *
 * Copyright (c) 2011-2013, R. Ramos <http://github.com/mung3r/>
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

import org.bukkit.entity.LivingEntity;

import com.edwardhand.mobrider.rider.Rider;

public class AttackGoal extends LivingEntityGoal
{
    public AttackGoal(LivingEntity target)
    {
        super(target);
    }

    @Override
    protected void handleNoTarget(Rider rider)
    {
        setGoalDone(true);
        rider.setTarget(null);
    }

    @Override
    protected boolean isCloseToTarget(Rider rider, double range)
    {
        return isWithinRange(rider.getRide().getLocation(), getTarget().getLocation(), NEW_AI_DISTANCE_LIMIT);
    }

    @Override
    protected void handleTarget(Rider rider)
    {
        rider.setTarget(getTarget());
        updateSpeed(rider);
    }
}
