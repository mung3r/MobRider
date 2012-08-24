package com.edwardhand.mobrider.goals;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import com.edwardhand.mobrider.ConfigManager;
import com.edwardhand.mobrider.rider.Rider;

import couk.Adamki11s.Regios.Regions.Region;

public class RegiosGoal extends LocationGoal
{
    private Region region;

    public RegiosGoal(ConfigManager configManager, Region region, World world)
    {
        super(configManager, getDestination(region, world));
        this.region = region;
    }

    @Override
    public void update(Rider rider)
    {
        if (rider != null) {
            rider.setTarget(null);
            LivingEntity ride = rider.getRide();

            if (ride != null) {
                if (isWithinRange(ride.getLocation(), destination, rangeSquared) || isWithinRegion(rider.getPlayer())) {
                    isGoalDone = true;
                }
                else {
                    setPathEntity(rider, destination);
                    updateSpeed(rider);
                }
            }
        }
    }

    private boolean isWithinRegion(Player player)
    {
        return region.isPlayerInRegion(player);
    }

    private static Location getDestination(Region region, World world)
    {
        Location midPoint = null;

        if (region != null && world != null) {

            Location minPoint = region.getL1();
            Location maxPoint = region.getL2();

            double x = (minPoint.getX() + maxPoint.getX()) / 2;
            double z = (minPoint.getZ() + maxPoint.getZ()) / 2;

            midPoint = world.getHighestBlockAt((int) x, (int) z).getLocation();
        }

        return midPoint;
    }
}
