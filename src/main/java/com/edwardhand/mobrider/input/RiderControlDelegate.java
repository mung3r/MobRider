package com.edwardhand.mobrider.input;

import org.getspout.spoutapi.event.input.KeyBindingEvent;
import org.getspout.spoutapi.gui.ScreenType;
import org.getspout.spoutapi.keyboard.BindingExecutionDelegate;

import com.edwardhand.mobrider.MobRider;
import com.edwardhand.mobrider.managers.GoalManager;
import com.edwardhand.mobrider.managers.RiderManager;

public class RiderControlDelegate implements BindingExecutionDelegate
{
    private GoalManager goalManager;
    private RiderManager riderManager;

    public RiderControlDelegate(MobRider plugin)
    {
        goalManager = plugin.getGoalManager();
        riderManager = plugin.getRiderManager();
    }

    @Override
    public void keyPressed(KeyBindingEvent event)
    {
        MobRider.getMRLogger().info("Key: " + event.getBinding().getDefaultKey());

        if (event.getPlayer().getActiveScreen() == ScreenType.GAME_SCREEN && riderManager.isRider(event.getPlayer())) {
            goalManager.setDirection(riderManager.getRider(event.getPlayer()), event.getBinding().getDefaultKey());
        }
    }

    @Override
    public void keyReleased(KeyBindingEvent event)
    {
        // do nothing
    }
}
