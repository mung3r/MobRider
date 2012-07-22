package com.edwardhand.mobrider.goals;

import com.edwardhand.mobrider.models.Rider;


public interface Goal
{
    void executeUpdate(Rider rider);
    long getTimeCreated();
    boolean isWithinHysteresisThreshold();
    boolean isGoalDone();
}
