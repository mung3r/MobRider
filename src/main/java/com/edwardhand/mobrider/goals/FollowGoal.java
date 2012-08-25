package com.edwardhand.mobrider.goals;

import org.bukkit.entity.LivingEntity;

import com.edwardhand.mobrider.ConfigManager;
import com.edwardhand.mobrider.rider.Rider;

public class FollowGoal extends LocationGoal
{
    protected LivingEntity target;

    public FollowGoal(ConfigManager configManager, LivingEntity target)
    {
        super(configManager, target);
        this.target = target;
    }

    @Override
    public void update(Rider rider)
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
                    if (isWithinRange(ride.getLocation(), target.getLocation(), rangeSquared)) {
                        setPathEntity(rider, ride.getLocation());
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
