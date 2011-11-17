package com.edwardhand.mobrider.models;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;

public class EntityGoal extends BaseGoal
{
    private LivingEntity target;

    public EntityGoal(LivingEntity target)
    {
        this.target = target;
    }

    @Override
    public GoalType getGoalType()
    {
        return GoalType.ENTITY;
    }

    @Override
    public Location getLocation()
    {
        if (target != null)
            return target.getLocation();
        return null;
    }

    public LivingEntity getEntity()
    {
        return target;
    }

}
