package com.edwardhand.mobrider.goals;

import org.bukkit.Location;

import com.edwardhand.mobrider.MobRider;
import com.edwardhand.mobrider.managers.ConfigManager;
import com.edwardhand.mobrider.managers.GoalManager;
import com.edwardhand.mobrider.models.Rider;

public abstract class BasicGoal implements Goal
{
    private static final long HYSTERESIS_THRESHOLD = 250; // quarter second
                                                          // in milliseconds
    protected ConfigManager configManager;
    protected double rangeSquared;
    protected GoalManager goalManager;
    protected Location destination;
    protected long timeCreated;

    public BasicGoal(MobRider plugin, Location destination)
    {
        configManager = plugin.getConfigManager();
        goalManager = plugin.getGoalManager();
        this.destination = destination;
        rangeSquared = configManager.MOUNT_RANGE * configManager.MOUNT_RANGE;
        timeCreated = System.currentTimeMillis();
    }

    @Override
    public boolean isWithinHysteresisThreshold()
    {
        return (System.currentTimeMillis() - timeCreated) < HYSTERESIS_THRESHOLD;
    }

    @Override
    public long getTimeCreated() {
        return timeCreated;
    }

    @Override
    public void executeUpdate(Rider rider)
    {
    }
}
