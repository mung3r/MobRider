package com.edwardhand.mobrider.goals;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;

import com.edwardhand.mobrider.MobRider;
import com.edwardhand.mobrider.models.Rider;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.exceptions.TownyException;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.TownBlock;
import com.palmergames.bukkit.towny.object.TownyUniverse;

public class TownyGoal extends LocationGoal
{
    Town town;

    public TownyGoal(MobRider plugin, Town town)
    {
        super(plugin, getDestination(town));
        this.town = town;
    }

    @Override
    public void update(Rider rider)
    {
        if (rider != null) {
            rider.setTarget(null);
            LivingEntity ride = rider.getRide();

            if (ride != null) {
                if (isWithinRange(ride.getLocation(), destination, rangeSquared) || isWithinTown(ride.getLocation())) {
                    isGoalDone = true;
                }
                else {
                    setPathEntity(rider, destination);
                    updateSpeed(rider);
                }
            }
        }
    }

    private boolean isWithinTown(Location currentLocation)
    {
        boolean isWithinTown = false;

        try {
            TownBlock block = TownyUniverse.getTownBlock(currentLocation);
            if (block != null && block.getTown().getName().equals(town.getName())) {
                isWithinTown = true;
            }
        }
        catch (NotRegisteredException e) {
            MobRider.getMRLogger().warning("Town not registered");
        }

        return isWithinTown;
    }

    private static Location getDestination(Town town)
    {
        Location spawn = null;

        try {
            spawn = town.getSpawn();
        }
        catch (TownyException e) {
            MobRider.getMRLogger().warning("Town spawn not found");
        }
        return spawn;
    }
}