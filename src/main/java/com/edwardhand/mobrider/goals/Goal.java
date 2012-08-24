package com.edwardhand.mobrider.goals;

import com.edwardhand.mobrider.rider.Rider;


public interface Goal
{
    void update(Rider rider);
    long getTimeCreated();
    boolean isWithinHysteresisThreshold();
    boolean isGoalDone();
}
