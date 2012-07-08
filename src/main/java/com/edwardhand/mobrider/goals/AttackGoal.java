package com.edwardhand.mobrider.goals;

import org.bukkit.entity.LivingEntity;

import com.edwardhand.mobrider.managers.ConfigManager;
import com.edwardhand.mobrider.managers.GoalManager;
import com.edwardhand.mobrider.models.Rider;

public class AttackGoal extends FollowGoal
{
    public AttackGoal(GoalManager goalManager, LivingEntity target)
    {
        super(goalManager, target);
        range = ConfigManager.ATTACK_RANGE * ConfigManager.ATTACK_RANGE;
    }

    @Override
    public void executeUpdate(Rider rider)
    {
        if (rider != null) {
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
                        rider.setTarget(target);
                    }
                    else {
                        goalManager.setPathEntity(rider, target.getLocation());
                    }
                    goalManager.updateSpeed(rider);
                }
            }
        }
    }
}
