package com.edwardhand.mobrider.goals;

import java.lang.reflect.Method;

import net.minecraft.server.EntityCreature;
import net.minecraft.server.Navigation;
import net.minecraft.server.PathEntity;
import net.minecraft.server.PathPoint;

import org.bukkit.Location;
import org.bukkit.craftbukkit.entity.CraftCreature;
import org.bukkit.craftbukkit.entity.CraftEnderDragon;
import org.bukkit.craftbukkit.entity.CraftGhast;
import org.bukkit.craftbukkit.entity.CraftSlime;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;

import com.edwardhand.mobrider.ConfigManager;
import com.edwardhand.mobrider.commons.EntityUtils;
import com.edwardhand.mobrider.commons.MRLogger;
import com.edwardhand.mobrider.rider.Rider;

public abstract class BasicGoal implements Goal
{
    protected static final double NEWAI_DISTANCE_LIMIT_SQUARED = 64.0D;
    protected static final long HYSTERESIS_THRESHOLD = 250; // quarter second
                                                            // in milliseconds
    protected ConfigManager configManager;
    protected double rangeSquared;
    protected long timeCreated;
    protected boolean isGoalDone;

    public BasicGoal(ConfigManager configManager)
    {
        this.configManager = configManager;
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

            if (EntityUtils.hasNewAI(ride)) {
                Location interimLocation = getInterimLocation(ride, destination);
                getNavigation(creature.getHandle()).a(interimLocation.getX(), interimLocation.getY(), interimLocation.getZ(), rider.getSpeed());
            }
            else {
                creature.getHandle().setPathEntity(new PathEntity(new PathPoint[] { new PathPoint(destination.getBlockX(), destination.getBlockY(), destination.getBlockZ()) }));
            }
        }
        else if (ride instanceof CraftSlime) {
            // TODO: implement setPathEntity for slime
            MRLogger.getInstance().debug("setPathEntity not implemented for Slime");
        }
        else if (ride instanceof CraftGhast) {
            // TODO: implement setPathEntity for ghast
            MRLogger.getInstance().debug("setPathEntity not implemented for Ghast");
        }
        else if (ride instanceof CraftEnderDragon) {
            // TODO: implement setPathEntity for enderdragon
            MRLogger.getInstance().debug("setPathEntity not implemented for EnderDragon");
        }
    }

    private static Navigation getNavigation(EntityCreature entityCreature)
    {
        try {
            Method m = EntityCreature.class.getMethod("getNavigation");
            return (Navigation) m.invoke((Object) entityCreature);
        }
        catch (Exception e) {
        }

        try {
            Method m = EntityCreature.class.getMethod("al");
            return (Navigation) m.invoke((Object) entityCreature);
        }
        catch (Exception e) {
        }

        return null;
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
