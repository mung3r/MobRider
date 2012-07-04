package com.edwardhand.mobrider.utils;

import java.util.Hashtable;
import java.util.Map;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import com.edwardhand.mobrider.MobRider;
import com.edwardhand.mobrider.models.Rider;

public class MRHandler implements Runnable
{
    private MRMetrics metrics;
    private Map<String, Rider> riders;

    public MRHandler(MobRider plugin)
    {
        metrics = plugin.getMetrics();
        riders = new Hashtable<String, Rider>();
    }

    public Rider addRider(Player player)
    {
        Rider rider = null;

        if (player != null) {
            String playerName = player.getName();
            rider = new Rider(playerName);
            riders.put(playerName, rider);

            if (rider.getRide() != null) {
                metrics.addCount(rider.getRide().getType());
            }
        }

        return rider != null ? rider : new Rider(null);
    }

    public Rider getRider(Player player)
    {
        Rider rider = null;

        if (player != null) {
            rider = riders.get(player.getName());
        }

        return rider != null ? rider : new Rider(null);
    }

    public Map<String, Rider> getRiders()
    {
        return riders;
    }

    public boolean isRider(Player player)
    {
        if (player != null) {
            Entity vehicle = player.getVehicle();
            if (vehicle instanceof LivingEntity) {
                if (!riders.containsKey(player.getName())) {
                    addRider(player);
                }
                return true;
            }
        }
        return false;
    }

    public void run()
    {
        for (String playerName : riders.keySet()) {
            Rider rider = riders.get(playerName);
            if (rider.isValid()) {
                rider.updateGoal();
            }
            else {
                rider.setTarget(null);
                riders.remove(playerName);
            }
        }
    }
}
