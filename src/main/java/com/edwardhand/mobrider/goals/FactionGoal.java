package com.edwardhand.mobrider.goals;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;

import com.edwardhand.mobrider.rider.Rider;
import com.massivecraft.factions.Board;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.Faction;

public class FactionGoal extends LocationGoal
{
    Faction faction;

    public FactionGoal(Faction faction)
    {
        super(getDestination(faction));
        this.faction = faction;
    }

    @Override
    public void update(Rider rider, double range)
    {
        if (rider != null) {
            rider.setTarget(null);
            LivingEntity ride = rider.getRide();

            if (ride != null) {
                if (isWithinRange(ride.getLocation(), destination, range) || isWithinFaction(ride.getLocation())) {
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

        Faction testFaction = Board.getFactionAt(new FLocation(currentLocation));
        if (testFaction.getId().equals(faction.getId())) {
            isWithinFaction = true;
        }

        return isWithinFaction;
    }

    private static Location getDestination(Faction faction)
    {
        return faction.getHome();
    }
}
