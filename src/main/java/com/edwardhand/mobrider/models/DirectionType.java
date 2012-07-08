package com.edwardhand.mobrider.models;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.util.Vector;

import com.edwardhand.mobrider.managers.ConfigManager;

public enum DirectionType {
    NORTH(new Vector(0, 0, -1), Arrays.asList("north", "n")),
    SOUTH(new Vector(0, 0, 1), Arrays.asList("south", "s")),
    EAST(new Vector(1, 0, 0), Arrays.asList("east", "e")),
    WEST(new Vector(-1, 0, 0), Arrays.asList("west", "w")),
    NORTHEAST(new Vector(1, 0, -1), Arrays.asList("northeast", "ne")),
    SOUTHEAST(new Vector(1, 0, 1), Arrays.asList("southeast", "se")),
    NORTHWEST(new Vector(-1, 0, -1), Arrays.asList("northwest", "nw")),
    SOUTHWEST(new Vector(-1, 0, 1), Arrays.asList("southwest", "sw"));

    private Vector direction;
    private List<String> names;

    private static final Map<String, DirectionType> nameMap = new HashMap<String, DirectionType>();

    static {
        for (DirectionType type : DirectionType.values()) {
            for (String name : type.names) {
                nameMap.put(name, type);
            }
        }
    }

    private DirectionType(Vector direction, List<String> names)
    {
        this.direction = direction.normalize().multiply(ConfigManager.MAX_TRAVEL_DISTANCE);
        this.names = names;
    }

    public Vector getDirection()
    {
        return direction.clone();
    }

    public static DirectionType fromName(String name)
    {
        return nameMap.get(name.toLowerCase());
    }
}
