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
import com.edwardhand.mobrider.metrics.Metrics.Graph;
import com.edwardhand.mobrider.metrics.Metrics.Plotter;


public class MetricsManager
{
    private Metrics metrics;
    private Map<EntityType, Integer> rideTypeCount;

    @SuppressWarnings("serial")
    public MetricsManager(Plugin plugin)
    {
        rideTypeCount = new Hashtable<EntityType, Integer>() {
            @Override
            public synchronized Integer get(Object key)
            {
                if (!super.containsKey(key)) {
                    return Integer.valueOf(0);
                }
                return super.get(key);
            };
        };

        try {
            metrics = new Metrics(plugin);
            setupGraphs();
            metrics.start();
        }
        catch (IOException e) {
            LoggerUtil.getInstance().warning("Metrics failed to load.");
        }

    }

    private void setupGraphs()
    {
        Graph graph = metrics.createGraph("Ride Types");

        for (EntityType rideType : EntityType.values()) {
            String name = rideType == EntityType.PLAYER ? "Player" : rideType.getName();

            if (rideType.isAlive()) {
                graph.addPlotter(new Plotter(name) {
                    @Override
                    public int getValue()
                    {
                        EntityType rideType = "Player".equals(getColumnName()) ? EntityType.PLAYER : EntityType.fromName(getColumnName());
                        Integer count = rideTypeCount.get(rideType);
                        rideTypeCount.put(rideType, Integer.valueOf(0));
                        return count;
                    }
                });
            }
        }
    }

    public void addCount(EntityType rideType)
    {
        rideTypeCount.put(rideType, rideTypeCount.get(rideType) + 1);
    }
}
