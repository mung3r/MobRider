package com.edwardhand.mobrider.goals;

import org.bukkit.entity.LivingEntity;

import com.edwardhand.mobrider.MobRider;
import com.edwardhand.mobrider.models.Rider;

public class AttackGoal extends FollowGoal
{
    public AttackGoal(MobRider plugin, LivingEntity target)
    {
        super(plugin, target);
        rangeSquared = plugin.getConfigManager().ATTACK_RANGE * plugin.getConfigManager().ATTACK_RANGE;
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
                    if (goalManager.isGoalWithinRange(ride.getLocation(), target.getLocation(), rangeSquared)) {
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