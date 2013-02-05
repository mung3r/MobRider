/*
 * This file is part of MobRider.
 *
 * Copyright (c) 2011-2012, R. Ramos <http://github.com/mung3r/>
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
package com.edwardhand.mobrider.metrics;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;

import org.bukkit.entity.EntityType;
import org.bukkit.plugin.Plugin;

import com.edwardhand.mobrider.commons.LoggerUtil;
import com.edwardhand.mobrider.mcstats.Metrics;
import com.edwardhand.mobrider.mcstats.Metrics.Graph;
import com.edwardhand.mobrider.mcstats.Metrics.Plotter;

public class RideMetrics
{
    private Map<String, Integer> typeCount;
    private Metrics metrics;
    private Graph graph;

    public RideMetrics(Plugin plugin)
    {
        try {
            metrics = new Metrics(plugin);
        }
        catch (IOException e) {
            LoggerUtil.getInstance().warning("Metrics failed to load.");
        }

        if (metrics != null) {
            typeCount = new Hashtable<String, Integer>();
            graph = metrics.createGraph("Ride Types");
            metrics.start();
        }
    }

    public void addCount(EntityType rideType)
    {
        addCount(getRideTypeName(rideType));
    }

    private void addCount(String name)
    {
        if (name == null) {
            LoggerUtil.getInstance().warning("Null type name passed into metrics.");
            return;
        }

        if (graph != null) {
            if (!typeCount.containsKey(name)) {
                typeCount.put(name, 0);
                graph.addPlotter(new Plotter(name) {
                    @Override
                    public int getValue()
                    {
                        return typeCount.get(getColumnName());
                    }
                });
            }

            typeCount.put(name, typeCount.get(name) + 1);
        }
    }

    private String getRideTypeName(EntityType rideType)
    {
        return rideType.getName() != null ? rideType.getName() : rideType.toString();
    }
}
