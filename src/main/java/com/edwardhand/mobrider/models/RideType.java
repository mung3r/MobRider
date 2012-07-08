package com.edwardhand.mobrider.models;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.EntityType;

public class RideType
{
    private EntityType type;
    private float speed;
    private String noise;
    private double chance;

    private static final Map<EntityType, RideType> typeMap = new HashMap<EntityType, RideType>();

    static {
        typeMap.put(EntityType.PLAYER, new RideType(EntityType.PLAYER, 0.0F, "", 100.0D));
    }

    public RideType(EntityType type, float speed, String noise, double chance)
    {
        this.type = type;
        this.speed = speed;
        this.noise = noise;
        this.chance = chance;

        typeMap.put(type, this);
    }

    public EntityType getCreatureType()
    {
        return type;
    }

    public float getMaxSpeed()
    {
        return speed;
    }

    public String getNoise()
    {
        return noise;
    }

    public double getChance()
    {
        return chance;
    }

    public static RideType fromType(EntityType type)
    {
        return typeMap.get(type);
    }
}
