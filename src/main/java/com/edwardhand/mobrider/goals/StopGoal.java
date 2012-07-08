package com.edwardhand.mobrider.goals;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;

import com.edwardhand.mobrider.MobRider;
import com.edwardhand.mobrider.models.Rider;

public class StopGoal extends BasicGoal implements Goal
{
    public StopGoal(MobRider plugin, Location destination)
    {
        super(plugin, destination);
    }

    @Override
    public void executeUpdate(Rider rider)
    {
        if (rider != null) {
            rider.setTarget(null);
            LivingEntity ride = rider.getRide();

            if (ride != null) {
                goalManager.setPathEntity(rider, destination);
            }
        }
    }
}
