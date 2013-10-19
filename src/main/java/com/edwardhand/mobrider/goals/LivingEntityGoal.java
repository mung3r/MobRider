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

public class LivingEntityGoal extends AbstractGoal
{
    private LivingEntity target;

    public LivingEntityGoal(LivingEntity target)
    {
        this.target = target;
    }

    public LivingEntity getTarget()
    {
        return target;
    }

    public void setTarget(LivingEntity target)
    {
        this.target = target;
    }

    @Override
    public void update(Rider rider, double range)
    {
        if (rider != null) {
            rider.setTarget(null);

            if (target == null) {
                handleNoTarget(rider);
            }
            else {
                if (target.isDead()) {
                    handleDeadTarget(rider);
                }
                else {
                    if (isCloseToTarget(rider, range)) {
                        handleTarget(rider);
                    }
                    else {
                        doTravel(rider);
                    }
                }
            }
        }
    }

    protected void handleNoTarget(Rider rider)
    {
        setGoalDone(true);
    }

    protected void handleDeadTarget(Rider rider)
    {
        target = null;
        setGoalDone(true);
    }

    protected boolean isCloseToTarget(Rider rider, double range)
    {
        return isWithinRange(rider.getRide().getLocation(), target.getLocation(), range);
    }

    protected void handleTarget(Rider rider)
    {
        setGoalDone(true);
    }

    protected void doTravel(Rider rider)
    {
        setPathEntity(rider, target.getLocation());
        updateSpeed(rider);
    }
}
