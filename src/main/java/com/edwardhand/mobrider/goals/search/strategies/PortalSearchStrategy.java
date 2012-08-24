package com.edwardhand.mobrider.goals.search.strategies;

import org.bukkit.Location;

import com.edwardhand.mobrider.ConfigManager;
import com.edwardhand.mobrider.commons.DependencyUtils;
import com.edwardhand.mobrider.goals.LocationGoal;
import com.edwardhand.mobrider.rider.Rider;
import com.onarandombox.MultiverseCore.api.MVDestination;

public class PortalSearchStrategy extends LocationSearchStrategy
{
    public PortalSearchStrategy(ConfigManager configManager)
    {
        super(configManager);
    }

    @Override
    public boolean find(Rider rider, String portalName)
    {
        boolean foundPortal = false;

        if (DependencyUtils.hasMultiverse()) {
            MVDestination portalTest = DependencyUtils.getMVDestinationFactory().getDestination("p:" + portalName);

            if (portalTest.getType().equals("Portal")) {
                Location portalLocation = portalTest.getLocation(null);
                if (portalLocation.getWorld().equals(rider.getWorld())) {
                    rider.setGoal(new LocationGoal(configManager, portalLocation));
                    foundPortal = true;
                }
            }
        }

        return foundPortal;
    }
}
