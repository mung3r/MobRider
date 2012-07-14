package com.edwardhand.mobrider.goals;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;

import com.edwardhand.mobrider.MobRider;
import com.edwardhand.mobrider.models.Rider;
import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class RegionGoal extends LocationGoal
{
    protected ProtectedRegion region;

    public RegionGoal(MobRider plugin, ProtectedRegion region, World world)
    {
        super(plugin, getMidPoint(region, world));
        this.region = region;
    }

    @Override
    public void executeUpdate(Rider rider)
    {
        if (rider != null) {
            rider.setTarget(null);
            LivingEntity ride = rider.getRide();

            if (ride != null) {
                if (goalManager.isWithinRange(ride.getLocation(), destination, rangeSquared) || goalManager.isWithinRegion(ride.getLocation(), region)) {
                    goalManager.setStopGoal(rider);
                }
                else {
                    goalManager.setPathEntity(rider, destination);
                    goalManager.updateSpeed(rider);
                }
            }
        }
    }

    private static Location getMidPoint(ProtectedRegion region, World world)
    {
        Location midPoint = null;

        if (region != null && world != null) {

            BlockVector min = region.getMinimumPoint();
            BlockVector max = region.getMaximumPoint();

            double x = min.getX() + max.getX() / 2;
            double y = min.getY() + max.getY() / 2;
            double z = min.getZ() + max.getZ() / 2;

            midPoint = world.getHighestBlockAt(new Location(world, x, y, z)).getLocation();
        }

        return midPoint;
    }
}
