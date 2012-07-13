package com.edwardhand.mobrider.goals;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;

import com.edwardhand.mobrider.MobRider;
import com.edwardhand.mobrider.models.Rider;

public class LocationGoal extends BasicGoal
{
    public LocationGoal(MobRider plugin, Location destination)
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
                if (goalManager.isWithinRange(ride.getLocation(), destination, rangeSquared)) {
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
