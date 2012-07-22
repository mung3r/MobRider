package com.edwardhand.mobrider.goals;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;

import com.bekvon.bukkit.residence.protection.ClaimedResidence;
import com.bekvon.bukkit.residence.protection.CuboidArea;
import com.edwardhand.mobrider.MobRider;
import com.edwardhand.mobrider.models.Rider;

public class ResidenceGoal extends LocationGoal
{
    private ClaimedResidence residence;

    public ResidenceGoal(MobRider plugin, ClaimedResidence residence, World world)
    {
        super(plugin, getDestination(residence));
        this.residence = residence;
    }

    @Override
    public void executeUpdate(Rider rider)
    {
        if (rider != null) {
            rider.setTarget(null);
            LivingEntity ride = rider.getRide();

            if (ride != null) {
                if (isWithinRange(ride.getLocation(), destination, rangeSquared) || isWithinResidence(ride.getLocation())) {
                    isGoalDone = true;
                }
                else {
                    setPathEntity(rider, destination);
                    updateSpeed(rider);
                }
            }
        }
    }

    private boolean isWithinResidence(Location location)
    {
        return residence.containsLoc(location);
    }

    private static Location getDestination(ClaimedResidence residence)
    {
        Location midPoint = null;

        if (residence.getAreaArray().length > 0) {
            midPoint = getMidPoint(residence.getAreaArray()[0]);
        }

        return midPoint;
    }

    private static Location getMidPoint(CuboidArea area)
    {
        Location midPoint = null;

        if (area != null) {
            Location lowLoc = area.getLowLoc();
            Location highLoc = area.getHighLoc();

            double x = lowLoc.getX() + highLoc.getX() / 2;
            double z = lowLoc.getZ() + highLoc.getZ() / 2;

            midPoint = area.getWorld().getHighestBlockAt((int) x, (int) z).getLocation();
        }

        return midPoint;
    }
}
