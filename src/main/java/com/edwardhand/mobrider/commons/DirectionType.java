/*
 * This file is part of MobRider.
 *
 * Copyright (c) 2011-2015, R. Ramos <http://github.com/mung3r/>
 * MobRider is licensed under the GNU Lesser General Public License.
 *
 * MobRider is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MobRider is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.edwardhand.mobrider.commons;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.util.Vector;

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
        this.direction = direction.normalize();
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
