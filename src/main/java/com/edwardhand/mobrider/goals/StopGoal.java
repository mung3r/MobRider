package com.edwardhand.mobrider.goals;

import org.bukkit.entity.LivingEntity;

import com.edwardhand.mobrider.rider.Rider;

public class StopGoal extends BasicGoal
{
    @Override
    public void update(Rider rider, double range)
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
