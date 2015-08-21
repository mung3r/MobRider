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
package com.edwardhand.mobrider.rider;

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
