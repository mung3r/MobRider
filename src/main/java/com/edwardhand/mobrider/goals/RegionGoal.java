package com.edwardhand.mobrider.goals;

import java.util.Iterator;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;

import com.edwardhand.mobrider.MobRider;
import com.edwardhand.mobrider.models.Rider;
import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class RegionGoal extends LocationGoal
{
    protected ProtectedRegion region;
    protected RegionManager regionManager;

    public RegionGoal(MobRider plugin, ProtectedRegion region, World world)
    {
        super(plugin, getDestination(region, world));
        this.region = region;
        regionManager = plugin.getRegionManager(world);
    }

    @Override
    public void update(Rider rider)
    {
        if (rider != null) {
            rider.setTarget(null);
            LivingEntity ride = rider.getRide();

            if (ride != null) {
                if (isWithinRange(ride.getLocation(), destination, rangeSquared) || isWithinRegion(ride.getLocation())) {
                    isGoalDone = true;
                }
                else {
                    setPathEntity(rider, destination);
                    updateSpeed(rider);
                }
            }
        }
    }

    private boolean isWithinRegion(Location currentLocation)
    {
        boolean isWithinRegion = false;

        Iterator<ProtectedRegion> regionIterator = regionManager.getApplicableRegions(currentLocation).iterator();
        while (regionIterator.hasNext()) {
            if (regionIterator.next().getId().equals(region.getId())) {
                isWithinRegion = true;
                break;
            }
        }

        return isWithinRegion;
    }

    private static Location getDestination(ProtectedRegion region, World world)
    {
        Location midPoint = null;

        if (region != null && world != null) {

            BlockVector minPoint = region.getMinimumPoint();
            BlockVector maxPoint = region.getMaximumPoint();

            double x = (minPoint.getX() + maxPoint.getX()) / 2;
            double z = (minPoint.getZ() + maxPoint.getZ()) / 2;

            midPoint = world.getHighestBlockAt((int) x, (int) z).getLocation();
        }

        return midPoint;
    }
}
