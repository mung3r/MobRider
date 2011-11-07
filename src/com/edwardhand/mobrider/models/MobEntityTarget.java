package com.edwardhand.mobrider.models;

import org.bukkit.Location;
import org.bukkit.entity.Entity;


public class MobEntityTarget  extends MobTarget
{
    private Entity _target;

    public MobEntityTarget(Entity target, MobIntent intent)
    {
        _target = target;
        _intent = intent;
    }
    
    public Entity getTarget()
    {
        return _target;
    }

    @Override
    public TargetType getType() 
    {
        return TargetType.ENTITY;
    }

    @Override
    public Location getLocation() 
    {
        if(_target != null)
            return _target.getLocation();
        return null;
    }

}
