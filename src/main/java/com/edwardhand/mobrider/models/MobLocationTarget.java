package com.edwardhand.mobrider.models;

import org.bukkit.Location;


public class MobLocationTarget extends MobTarget
{
    private Location _target;
    
    public MobLocationTarget(Location target, MobIntent intent)
    {
        _target = target;
        _intent = intent;
    }

    public Location getLocation()
    {
        return _target;
    }

    @Override
    public TargetType getType() 
    {
        return TargetType.LOCATION;
    }
}
