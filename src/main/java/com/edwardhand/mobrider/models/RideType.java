package com.edwardhand.mobrider.models;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.CreatureType;

public class RideType
{
    private CreatureType type;
    private float speed;
    private String noise;

    private static final Map<CreatureType, RideType> typeMap = new HashMap<CreatureType, RideType>();

    public RideType(CreatureType type, float speed, String noise)
    {
        this.type = type;
        this.speed = speed;
        this.noise = noise;

        typeMap.put(type, this);
    }

    public CreatureType getCreatureType()
    {
        return type;
    }

    public float getSpeed()
    {
        return speed;
    }

    public String getNoise()
    {
        return noise;
    }

    public static RideType fromType(CreatureType type)
    {
        return typeMap.get(type);
    }
}
