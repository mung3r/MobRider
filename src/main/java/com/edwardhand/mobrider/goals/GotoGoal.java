package com.edwardhand.mobrider.goals;

import org.bukkit.entity.LivingEntity;

import com.edwardhand.mobrider.rider.Rider;

public class GotoGoal extends LocationGoal
{
    protected LivingEntity target;

    public GotoGoal(LivingEntity target)
    {
        super(target);
        this.target = target;
    }

    @Override
    public void update(Rider rider, double range)
    {
        if (rider != null) {
            rider.setTarget(null);
            LivingEntity ride = rider.getRide();

            if (target == null) {
                isGoalDone = true;
            }
            else {
                if (target.isDead()) {
                    target = null;
                    isGoalDone = true;
                }
                else {
                    if (isWithinRange(ride.getLocation(), target.getLocation(), range)) {
                        isGoalDone = true;
                    }
                    else {
                        setPathEntity(rider, target.getLocation());
                        updateSpeed(rider);
                    }
                }
            }
        }
    }
}
