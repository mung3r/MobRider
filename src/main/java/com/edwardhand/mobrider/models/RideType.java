package com.edwardhand.mobrider.models;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.EntityType;

public class RideType
{
    private EntityType type;
    private float speed;
    private String noise;

    private static final Map<EntityType, RideType> typeMap = new HashMap<EntityType, RideType>();

    public RideType(EntityType type, float speed, String noise)
    {
        this.type = type;
        this.speed = speed;
        this.noise = noise;

        typeMap.put(type, this);
    }

    public EntityType getCreatureType()
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

    public static RideType fromType(EntityType type)
    {
        return typeMap.get(type);
    }
}
