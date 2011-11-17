package com.edwardhand.mobrider.models;

import org.bukkit.Location;

public class LocationGoal extends BaseGoal
{
    private Location target;

    public LocationGoal(Location target)
    {
        this.target = target;
    }

    @Override
    public GoalType getGoalType()
    {
        return GoalType.LOCATION;
    }

    @Override
    public Location getLocation()
    {
        return target;
    }
}
