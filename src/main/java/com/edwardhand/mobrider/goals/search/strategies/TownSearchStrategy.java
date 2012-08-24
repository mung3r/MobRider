package com.edwardhand.mobrider.goals.search.strategies;

import com.edwardhand.mobrider.ConfigManager;
import com.edwardhand.mobrider.commons.DependencyUtils;
import com.edwardhand.mobrider.goals.TownyGoal;
import com.edwardhand.mobrider.rider.Rider;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.TownyUniverse;

public class TownSearchStrategy extends LocationSearchStrategy
{
    public TownSearchStrategy(ConfigManager configManager)
    {
        super(configManager);
    }

    @Override
    public boolean find(Rider rider, String townName)
    {
        boolean foundTown = false;

        if (DependencyUtils.hasTowny()) {
            for (Town town : TownyUniverse.getDataSource().getTowns()) {
                if (town.getName().equals(townName)) {
                    rider.setGoal(new TownyGoal(configManager, town));
                    foundTown = true;
                }
            }
        }

        return foundTown;
    }
}
