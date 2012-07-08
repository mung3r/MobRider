package com.edwardhand.mobrider.goals;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;

import com.edwardhand.mobrider.managers.GoalManager;
import com.edwardhand.mobrider.models.Rider;

public class LocationGoal extends BasicGoal implements Goal
{
    public LocationGoal(GoalManager goalManager, Location destination)
    {
        super(goalManager, destination);
    }

    @Override
    public void executeUpdate(Rider rider)
    {
        if (rider != null) {
            rider.setTarget(null);
            LivingEntity ride = rider.getRide();

            if (ride != null) {
                if (goalManager.isWithinRange(ride.getLocation(), destination, range)) {
                    goalManager.setStopGoal(rider);
                }
                else {
                    goalManager.setPathEntity(rider, destination);
                    goalManager.updateSpeed(rider);
                }
            }
        }
    }
}
