package com.edwardhand.mobrider.goals;

import net.minecraft.server.PathEntity;
import net.minecraft.server.PathPoint;

import org.bukkit.Location;
import org.bukkit.craftbukkit.entity.CraftCreature;
import org.bukkit.craftbukkit.entity.CraftEnderDragon;
import org.bukkit.craftbukkit.entity.CraftGhast;
import org.bukkit.craftbukkit.entity.CraftSlime;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;

import com.edwardhand.mobrider.MobRider;
import com.edwardhand.mobrider.managers.ConfigManager;
import com.edwardhand.mobrider.models.Rider;
import com.edwardhand.mobrider.utils.MRUtil;

public abstract class BasicGoal implements Goal
{
    protected static final double NEWAI_DISTANCE_LIMIT_SQUARED = 64.0D;
    protected static final long HYSTERESIS_THRESHOLD = 250; // quarter second
                                                          // in milliseconds
    protected ConfigManager configManager;
    protected double rangeSquared;
    protected long timeCreated;
    protected boolean isGoalDone;

    public BasicGoal(MobRider plugin)
    {
        configManager = plugin.getConfigManager();
        rangeSquared = configManager.MOUNT_RANGE * configManager.MOUNT_RANGE;
        timeCreated = System.currentTimeMillis();
        isGoalDone = false;
    }

    @Override
    public boolean isWithinHysteresisThreshold()
    {
        return (System.currentTimeMillis() - timeCreated) < HYSTERESIS_THRESHOLD;
    }

    @Override
    public long getTimeCreated()
    {
        return timeCreated;
    }

    @Override
    public boolean isGoalDone()
    {
        return isGoalDone;
    }

    @Override
    public void update(Rider rider)
    {
    }

    protected static void setPathEntity(Rider rider, Location destination)
    {
        LivingEntity ride = rider.getRide();

        if (ride instanceof CraftCreature) {
            CraftCreature creature = (CraftCreature) ride;

            if (MRUtil.hasNewAI(ride)) {
                Location interimLocation = getInterimLocation(ride, destination);
                creature.getHandle().al().a(interimLocation.getX(), interimLocation.getY(), interimLocation.getZ(), rider.getSpeed());
            }
            else {
                ((CraftCreature) ride).getHandle().setPathEntity(new PathEntity(new PathPoint[] { new PathPoint(destination.getBlockX(), destination.getBlockY(), destination.getBlockZ()) }));
            }
        }
        else if (ride instanceof CraftSlime) {
            // TODO: implement setPathEntity for slime
        }
        else if (ride instanceof CraftGhast) {
            // TODO: implement setPathEntity for ghast
        }
        else if (ride instanceof CraftEnderDragon) {
            // TODO: implement setPathEntity for enderdragon
        }
    }

    private static Location getInterimLocation(LivingEntity ride, Location destination)
    {
        Location interimTarget = null;

        if (ride != null && ride.getLocation().getWorld().equals(destination.getWorld()) && ride.getLocation().distanceSquared(destination) > NEWAI_DISTANCE_LIMIT_SQUARED) {
            interimTarget = ride.getLocation().clone().add(new Vector(destination.getX() - ride.getLocation().getX(), destination.getY() - ride.getLocation().getY(), destination.getZ() - ride.getLocation().getZ()).normalize().multiply(8));
        }
        else {
            interimTarget = destination;
        }

        return interimTarget;
    }
}
