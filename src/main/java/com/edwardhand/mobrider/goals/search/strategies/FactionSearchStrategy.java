package com.edwardhand.mobrider.goals.search.strategies;

import com.edwardhand.mobrider.ConfigManager;
import com.edwardhand.mobrider.commons.DependencyUtils;
import com.edwardhand.mobrider.goals.FactionGoal;
import com.edwardhand.mobrider.rider.Rider;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;

public class FactionSearchStrategy extends LocationSearchStrategy
{

    public FactionSearchStrategy(ConfigManager configManager)
    {
        super(configManager);
    }

    @Override
    public boolean find(Rider rider, String factionTag)
    {
        boolean foundFaction = false;

        if (DependencyUtils.hasFactions()) {
            Faction faction = Factions.i.getByTag(factionTag);
            if (faction != null) {
                rider.setGoal(new FactionGoal(configManager, faction));
                foundFaction = true;
            }
        }

        return foundFaction;
    }
}
