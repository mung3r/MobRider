package com.edwardhand.mobrider.goals;

import org.bukkit.entity.LivingEntity;

import com.edwardhand.mobrider.MobRider;
import com.edwardhand.mobrider.models.Rider;

public class StopGoal extends BasicGoal implements Goal
{
    public StopGoal(MobRider plugin)
    {
        super(plugin);
    }

    @Override
    public void update(Rider rider)
    {
        if (rider != null) {
            rider.setTarget(null);
            LivingEntity ride = rider.getRide();

            if (ride != null) {
                setPathEntity(rider, rider.getRide().getLocation());
            }
        }
    }
}
