package com.edwardhand.mobrider.utils;

import java.util.Hashtable;
import java.util.Map;

import org.bukkit.entity.Player;

import com.edwardhand.mobrider.models.Rider;

public class MRHandler implements Runnable
{
    private Map<String, Rider> riders;

    public MRHandler()
    {
        riders = new Hashtable<String, Rider>();
    }

    public Rider addRider(Player player)
    {
        Rider rider = null;

        if (player != null) {
            String playerName = player.getName();
            rider = new Rider(playerName);
            riders.put(playerName, rider);
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
        return player != null && riders.containsKey(player.getName());
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
