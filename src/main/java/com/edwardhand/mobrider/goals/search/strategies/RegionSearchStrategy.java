package com.edwardhand.mobrider.goals.search.strategies;

import com.edwardhand.mobrider.commons.DependencyUtils;
import com.edwardhand.mobrider.goals.RegionGoal;
import com.edwardhand.mobrider.rider.Rider;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class RegionSearchStrategy extends LocationSearchStrategy
{
    @Override
    public boolean find(Rider rider, String regionName)
    {
        boolean foundRegion = false;

        if (DependencyUtils.hasWorldGuard()) {
            ProtectedRegion region = DependencyUtils.getRegionManager(rider.getWorld()).getRegion(regionName);
            if (region != null) {
                rider.setGoal(new RegionGoal(region, rider.getWorld()));
                foundRegion = true;
            }
        }

        return foundRegion;
    }
}
