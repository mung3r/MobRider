package com.edwardhand.mobrider.metrics;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;

import org.bukkit.entity.EntityType;
import org.bukkit.plugin.Plugin;


public class MetricsManager extends Metrics
{
    private Map<EntityType, Integer> rideTypeCount;

    public MetricsManager(Plugin plugin) throws IOException
    {
        super(plugin);
        rideTypeCount = new Hashtable<EntityType, Integer>() {
            private static final long serialVersionUID = 1L;

            @Override
            public synchronized Integer get(Object key)
            {
                if (!super.containsKey(key)) {
                    return Integer.valueOf(0);
                }
                return super.get(key);
            };
        };
    }

    public void setupGraphs()
    {
        Graph graph = createGraph("Ride Types");

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
