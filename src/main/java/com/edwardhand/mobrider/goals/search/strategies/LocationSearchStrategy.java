package com.edwardhand.mobrider.goals.search.strategies;

import com.edwardhand.mobrider.ConfigManager;
import com.edwardhand.mobrider.goals.search.LocationSearch;
import com.edwardhand.mobrider.rider.Rider;

public class LocationSearchStrategy implements LocationSearch
{
    protected ConfigManager configManager;

    public LocationSearchStrategy(ConfigManager configManager)
    {
        this.configManager = configManager;
    }

    @Override
    public boolean find(Rider rider, String locationName)
    {
        return false;
    }
}
