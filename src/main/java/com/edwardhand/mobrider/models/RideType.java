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
    private double cost;

    private static final Map<EntityType, RideType> typeMap = new HashMap<EntityType, RideType>();

    static {
        typeMap.put(EntityType.PLAYER, new RideType(EntityType.PLAYER, 0.0F, "", 100.0D, 0.0));
    }

    public RideType(EntityType type, float speed, String noise, double chance, double cost)
    {
        this.type = type;
        this.speed = speed;
        this.noise = noise;
        this.chance = chance;
        this.cost = cost;

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

    public double getCost()
    {
        return cost;
    }

    public static RideType fromType(EntityType type)
    {
        return typeMap.get(type);
    }
}
