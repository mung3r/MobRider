package com.edwardhand.mobrider.goals;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;

import com.edwardhand.mobrider.MobRider;
import com.edwardhand.mobrider.models.Rider;
import com.onarandombox.MultiversePortals.PortalLocation;

public class PortalGoal extends LocationGoal implements Goal
{
    PortalLocation portalLocation;

    public PortalGoal(MobRider plugin, PortalLocation portalLocation)
    {
        super(plugin, getMidPoint(portalLocation));
        this.portalLocation = portalLocation;
    }

    @Override
    public void executeUpdate(Rider rider)
    {
        if (rider != null) {
            rider.setTarget(null);
            LivingEntity ride = rider.getRide();

            if (ride != null) {
                if (isWithinRange(ride.getLocation(), destination, rangeSquared) || isWithinPortal(ride.getLocation())) {
                    goalManager.setStopGoal(rider);
                }
                else {
                    goalManager.setPathEntity(rider, destination);
                    goalManager.updateSpeed(rider);
                }
            }
        }
    }

    private boolean isWithinPortal(Location currentLocation)
    {
        Vector min = portalLocation.getMinimum();
        Vector max = portalLocation.getMaximum();

        return currentLocation.getWorld().equals(portalLocation.getMVWorld().getCBWorld()) && 
            currentLocation.getX() > min.getX() && currentLocation.getX() < max.getX() &&
            currentLocation.getY() > min.getY() && currentLocation.getY() < max.getY() &&
            currentLocation.getZ() > min.getZ() && currentLocation.getZ() < max.getZ();
    }

    private static Location getMidPoint(PortalLocation portalLocation)
    {
        Location midPoint = null;

        if (portalLocation != null) {

            Vector min = portalLocation.getMinimum();
            Vector max = portalLocation.getMaximum();

            double x = min.getX() + max.getX() / 2;
            double y = min.getY() + max.getY() / 2;
            double z = min.getZ() + max.getZ() / 2;

            World world = portalLocation.getMVWorld().getCBWorld();
            midPoint = world.getHighestBlockAt(new Location(world, x, y, z)).getLocation();
        }

        return midPoint;
    }
}
