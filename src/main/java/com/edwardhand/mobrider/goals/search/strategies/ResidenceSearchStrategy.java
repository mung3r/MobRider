package com.edwardhand.mobrider.goals.search.strategies;

import com.bekvon.bukkit.residence.Residence;
import com.bekvon.bukkit.residence.protection.ClaimedResidence;
import com.edwardhand.mobrider.ConfigManager;
import com.edwardhand.mobrider.commons.DependencyUtils;
import com.edwardhand.mobrider.goals.ResidenceGoal;
import com.edwardhand.mobrider.rider.Rider;

public class ResidenceSearchStrategy extends LocationSearchStrategy
{
    public ResidenceSearchStrategy(ConfigManager configManager)
    {
        super(configManager);
    }

    @Override
    public boolean find(Rider rider, String residenceName)
    {
        boolean foundResidence = false;

        if (DependencyUtils.hasResidence()) {
            ClaimedResidence residence = Residence.getResidenceManager().getByName(residenceName);
            if (residence != null && residence.getWorld().equals(rider.getWorld().getName())) {
                rider.setGoal(new ResidenceGoal(configManager, residence));
                foundResidence = true;
            }
        }

        return foundResidence;
    }
}
