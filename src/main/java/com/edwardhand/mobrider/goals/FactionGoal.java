package com.edwardhand.mobrider.goals;

import org.bukkit.Location;

import com.edwardhand.mobrider.MobRider;
import com.massivecraft.factions.Faction;

public class FactionGoal extends LocationGoal
{

    public FactionGoal(MobRider plugin, Faction faction)
    {
        super(plugin, getDestination(faction));
        // TODO Auto-generated constructor stub
    }

    private static Location getDestination(Faction faction)
    {
        // TODO Auto-generated method stub
        return null;
    }

}
