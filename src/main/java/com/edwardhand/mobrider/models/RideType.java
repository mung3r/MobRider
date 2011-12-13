package com.edwardhand.mobrider.models;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.CreatureType;

public enum RideType {
    BLAZE(CreatureType.BLAZE, 0.0F, "Kaboom"),
    CAVE_SPIDER(CreatureType.CAVE_SPIDER, 0.4F, "Cheeuuuk"),
    CHICKEN(CreatureType.CHICKEN, 0.2F, "Cluck"),
    COW(CreatureType.COW, 0.3F, "Mooooo"),
    CREEPER(CreatureType.CREEPER, 0.6F, "Hisssss"),
    ENDER_DRAGON(CreatureType.ENDER_DRAGON, 0.0F, "Rawwwwwwr"),
    ENDERMAN(CreatureType.ENDERMAN, 0.2F, "Mooooaaan"),
    GHAST(CreatureType.GHAST, 0.6F, "Hooooo"),
    GIANT(CreatureType.GIANT, 0.6F, "MOOOOAAAN"),
    MONSTER(CreatureType.MONSTER, 0.4F, "Rawr"),
    MUSHROOM_COW(CreatureType.MUSHROOM_COW, 0.0F, "Moo00sh"),
    PIG(CreatureType.PIG, 0.25F, "Oink"),
    PIG_ZOMBIE(CreatureType.PIG_ZOMBIE, 0.4F, "Mooooiiink"),
    SHEEP(CreatureType.SHEEP, 0.25F, "Baaaaa"),
    SILVERFISH(CreatureType.SILVERFISH, 0.4F, "Cheeuuuk"),
    SKELETON(CreatureType.SKELETON, 0.2F, "*silence*"),
    SLIME(CreatureType.SLIME, 0.2F, "Blluuurrrp"),
    SPIDER(CreatureType.SPIDER, 0.4F, "Cheeuuuk"),
    SQUID(CreatureType.SQUID, 1.0F, "Glub"),
    VILLAGER(CreatureType.VILLAGER, 0.0F, "Mhmm"),
    WOLF(CreatureType.WOLF, 0.8F, "Woof"),
    ZOMBIE(CreatureType.ZOMBIE,0.2F, "Mooooaaan");

    private CreatureType type;
    private float speed;
    private String noise;

    private static final Map<CreatureType, RideType> typeMap = new HashMap<CreatureType, RideType>();

    static {
        for (RideType type : RideType.values()) {
            typeMap.put(type.getCreatureType(), type);
        }
    }

    RideType(CreatureType type, float speed, String noise)
    {
        this.type = type;
        this.speed = speed;
        this.noise = noise;
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
