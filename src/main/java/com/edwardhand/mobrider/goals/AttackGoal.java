package com.edwardhand.mobrider.goals;

import org.bukkit.entity.LivingEntity;

import com.edwardhand.mobrider.ConfigManager;
import com.edwardhand.mobrider.rider.Rider;

public class AttackGoal extends FollowGoal
{
    public AttackGoal(ConfigManager configManager, LivingEntity target)
    {
        super(configManager, target);
    }

    @Override
    public void update(Rider rider)
    {
        if (rider != null) {
            LivingEntity ride = rider.getRide();

            if (target == null) {
                isGoalDone = true;
                rider.setTarget(null);
            }
            else {
                if (target.isDead()) {
                    target = null;
                    isGoalDone = true;
                }
                else {
                    if (isWithinRange(ride.getLocation(), target.getLocation(), NEWAI_DISTANCE_LIMIT_SQUARED)) {
                        rider.setTarget(target);
                    }
                    else {
                        setPathEntity(rider, target.getLocation());
                    }
                    updateSpeed(rider);
                }
            }
        }
    }
}
