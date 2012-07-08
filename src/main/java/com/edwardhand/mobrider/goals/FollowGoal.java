package com.edwardhand.mobrider.goals;

import org.bukkit.entity.LivingEntity;

import com.edwardhand.mobrider.managers.GoalManager;
import com.edwardhand.mobrider.models.Rider;

public class FollowGoal extends LocationGoal
{
    protected LivingEntity target;

    public FollowGoal(GoalManager goalManager, LivingEntity target)
    {
        super(goalManager, target.getLocation());
        this.target = target;
    }

    @Override
    public void executeUpdate(Rider rider)
    {
        if (rider != null) {
            rider.setTarget(null);
            LivingEntity ride = rider.getRide();

            if (target == null) {
                goalManager.setStopGoal(rider);
            }
            else {
                if (target.isDead()) {
                    target = null;
                    goalManager.setStopGoal(rider);
                }
                else {
                    if (goalManager.isWithinRange(ride.getLocation(), target.getLocation(), range)) {
                        goalManager.setPathEntity(rider, ride.getLocation());
                    }
                    else {
                        goalManager.setPathEntity(rider, target.getLocation());
                        goalManager.updateSpeed(rider);
                    }
                }
            }
        }
    }
}
