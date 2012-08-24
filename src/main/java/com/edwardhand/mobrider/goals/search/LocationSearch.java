package com.edwardhand.mobrider.goals.search;

import com.edwardhand.mobrider.rider.Rider;

public interface LocationSearch
{
    public boolean find(Rider rider, String locationName);
}
