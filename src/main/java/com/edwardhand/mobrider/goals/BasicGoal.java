package com.edwardhand.mobrider.goals;

import org.bukkit.Location;

import com.edwardhand.mobrider.managers.ConfigManager;
import com.edwardhand.mobrider.managers.GoalManager;
import com.edwardhand.mobrider.models.Rider;

public abstract class BasicGoal implements Goal
{
    protected double range;
    protected GoalManager goalManager;
    protected Location destination;

    public BasicGoal(GoalManager goalManager, Location destination)
    {
        this.goalManager = goalManager;
        this.destination = destination;
        range = ConfigManager.MOUNT_RANGE * ConfigManager.MOUNT_RANGE;
    }

    @Override
    public void executeUpdate(Rider rider)
    {
    }
}
