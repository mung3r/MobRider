package com.edwardhand.mobrider.goals;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;

import com.edwardhand.mobrider.ConfigManager;
import com.edwardhand.mobrider.rider.Rider;
import com.massivecraft.factions.Board;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.Faction;

public class FactionGoal extends LocationGoal
{
    Faction faction;

    public FactionGoal(ConfigManager configManager, Faction faction)
    {
        super(configManager, getDestination(faction));
        this.faction = faction;
    }

    @Override
    public void update(Rider rider)
    {
        if (rider != null) {
            rider.setTarget(null);
            LivingEntity ride = rider.getRide();

            if (ride != null) {
                if (isWithinRange(ride.getLocation(), destination, rangeSquared) || isWithinFaction(ride.getLocation())) {
                    isGoalDone = true;
                }
                else {
                    setPathEntity(rider, destination);
                    updateSpeed(rider);
                }
            }
        }
    }

    private boolean isWithinFaction(Location currentLocation)
    {
        boolean isWithinFaction = false;

        Faction faction = Board.getFactionAt(new FLocation(currentLocation));
        if (faction.getId().equals(this.faction.getId())) {
            isWithinFaction = true;
        }

        return isWithinFaction;
    }

    private static Location getDestination(Faction faction)
    {
        return faction.getHome();
    }
}
