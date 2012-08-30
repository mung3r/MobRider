package com.edwardhand.mobrider.metrics;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;

import org.bukkit.entity.EntityType;
import org.bukkit.plugin.Plugin;

import com.edwardhand.mobrider.commons.MRLogger;
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
            MRLogger.getInstance().warning("Metrics failed to load.");
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
