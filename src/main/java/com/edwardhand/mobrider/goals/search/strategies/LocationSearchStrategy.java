package com.edwardhand.mobrider.goals.search.strategies;

import com.edwardhand.mobrider.goals.search.LocationSearch;
import com.edwardhand.mobrider.rider.Rider;

public class LocationSearchStrategy implements LocationSearch
{
    @Override
    public boolean find(Rider rider, String locationName)
    {
        return false;
    }
}
