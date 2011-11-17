package com.edwardhand.mobrider.models;

import org.bukkit.Location;

public abstract class BaseGoal
{
    public enum GoalType {
        ENTITY, LOCATION;
    }

    public abstract GoalType getGoalType();
    public abstract Location getLocation();
}
