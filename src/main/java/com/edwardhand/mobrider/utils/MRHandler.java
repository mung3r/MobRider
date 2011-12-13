package com.edwardhand.mobrider.utils;

import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.entity.Player;

import com.edwardhand.mobrider.MobRider;
import com.edwardhand.mobrider.models.Ride;

public class MRHandler implements Runnable
{
    private MobRider plugin;
    private ConcurrentHashMap<String, Ride> rides;

    public MRHandler(MobRider plugin)
    {
        this.plugin = plugin;
        rides = new ConcurrentHashMap<String, Ride>();
    }

    public ConcurrentHashMap<String, Ride> getRides()
    {
        return rides;
    }

    public Ride getRide(org.bukkit.entity.Entity player)
    {
        Ride ride;

        if (player != null && player instanceof Player) {

            String playerName = ((Player) player).getName();

            if (rides.containsKey(playerName)) {
                ride = rides.get(playerName);
            }
            else {
                ride = new Ride(((CraftEntity) player).getHandle().vehicle);
                if (ride.isValid()) {
                    rides.put(playerName, ride);
                }
            }
        }
        else {
            ride = new Ride(null);
        }

        return ride;
    }

    public void run()
    {
        for (String playerName : rides.keySet()) {

            if (plugin.getServer().getPlayer(playerName) == null) {
                rides.remove(playerName);
            }
            else {
                Ride ride = rides.get(playerName);
                if (ride.hasRider()) {
                    ride.updateGoal();
                }
                else {
                    ride.setTarget(null);
                    rides.remove(playerName);
                }
            }
        }
    }
}
