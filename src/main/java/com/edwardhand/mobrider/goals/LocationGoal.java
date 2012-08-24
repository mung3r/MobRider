package com.edwardhand.mobrider.goals;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;

import com.edwardhand.mobrider.ConfigManager;
import com.edwardhand.mobrider.rider.Rider;

public class LocationGoal extends BasicGoal
{
    protected Location destination;

    public LocationGoal(ConfigManager configManager, Location destination)
    {
        super(configManager);
        this.destination = destination;
    }

    @Override
    public void update(Rider rider)
    {
        if (rider != null) {
            rider.setTarget(null);
            LivingEntity ride = rider.getRide();

            if (ride != null) {
                if (isWithinRange(ride.getLocation(), destination, rangeSquared)) {
                    isGoalDone = true;
                }
                else {
                    setPathEntity(rider, destination);
                    updateSpeed(rider);
                }
            }
        }
    }

    protected static boolean isWithinRange(Location start, Location end, double distanceSquared)
    {
        return !start.getWorld().equals(end.getWorld()) || start.distanceSquared(end) < distanceSquared;
    }

    protected static void updateSpeed(Rider rider)
    {
        if (rider != null) {
            LivingEntity ride = rider.getRide();

            if (ride != null && rider.getRideType() != null) {
                Vector velocity = ride.getVelocity();
                double saveY = velocity.getY();
                velocity.normalize().multiply(rider.getSpeed());
                velocity.setY(saveY);
                ride.setVelocity(velocity);
            }
        }
    }
}
